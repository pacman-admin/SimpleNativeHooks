package org.simplenativehooks.utilities;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Stack;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provide file reading and writing utilities
 * This is a static class. No instance should be created
 * @author HP Truong
 *
 */
public class FileUtility {

	private static final Logger LOGGER = Logger.getLogger(FileUtility.class.getName());

	/**
	 * Private constructor so that no instance is created
	 */
	private FileUtility() {
		throw new IllegalStateException("Cannot create an instance of static class FileUtility");
	}

	/**
	 * Create directory if not exists
	 * @param directory full path of the directory
	 * @return if creation succeed
	 */
	public static boolean createDirectory(String directory) {
		File theDir = new File(directory);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
            try {
				theDir.mkdirs();
            } catch (SecurityException se) {
				se.printStackTrace();
				// handle it
				return false;
			}
        }
        return true;
    }

	/**
	 * Read a plain text file.
	 * @param file file that will be read
	 * @return StringBuffer the read result.
	 */
	public static StringBuffer readFromFile(File file) {
		StringBuffer output = new StringBuffer();

        try (FileInputStream fr = new FileInputStream(file)) {

            InputStreamReader char_input = new InputStreamReader(fr, StandardCharsets.UTF_8.newDecoder());

            BufferedReader br = new BufferedReader(char_input);

            while (true) {
                String in = br.readLine();
                if (in == null) {
                    break;
                }
                output.append(in).append("\n");
            }

            br.close();

        } catch (IOException e) {
            Logger.getLogger(FileUtility.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
		return output;
	}

	/**
	 * Read a plain text file.
	 * @param filePath path to the file
	 * @return text content of the file
	 */
	public static StringBuffer readFromFile(String filePath) {
		return readFromFile(new File(filePath));
	}

	/**
	 * Extracts content from this JAR to the destination.
	 *
	 * @param path path in this JAR to explore files.
	 * @param destination the destination directory where files will be extracted to.
	 * @param filteringFunction the function to filter files by. Only files with returned value true will be extracted.
	 * @param postProcessingFunction to perform any post processing of the extracted file.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void extractFromCurrentJar(String path, File destination, Function<String, Boolean> filteringFunction, Function<String, Boolean> postProcessingFunction) throws IOException, URISyntaxException {
//		final File jarFile = new File(FileUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		final File jarFile = new File(FileUtility.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

		if (jarFile.isFile()) {// Run with JAR file
		    final JarFile jar = new JarFile(jarFile);
		    final Enumeration<JarEntry> entries = jar.entries(); // Gives ALL entries in jar
		    while(entries.hasMoreElements()) {
		    	JarEntry entry = entries.nextElement();
		        String name = entry.getName();

		        if (!name.startsWith(path + "/")) { // Filter according to the path
		        	continue;
		        }

		        if (!filteringFunction.apply(name)) {
		        	continue;
		        }

		        InputStream inputStream = jar.getInputStream(entry);
		        int prefixIndex = (path + "/").length();
		        Path destinationPath = Paths.get(FileUtility.joinPath(destination.getAbsolutePath(), name.substring(prefixIndex)));
		        if (entry.isDirectory()) {
		        	LOGGER.info("Creating " + destinationPath);
		        	destinationPath.toFile().mkdirs();
		    		continue;
		    	}

		        if (!destinationPath.getParent().toFile().exists()) {
					destinationPath.getParent().toFile().mkdirs();
				}
		        Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		        if (!postProcessingFunction.apply(destinationPath.toString())) {
		        	LOGGER.warning("Failed to apply post processing function to path " + destination);
		        }
		    }
		    jar.close();
		} else { // Run with IDE
			final URL url = FileUtility.class.getResource("/" + path);
			if (url == null) {
				return;
			}
			try {
				File root = new File(url.toURI());
				Path rootPath = root.toPath();
				Stack<File> dirs = new Stack<>();
				dirs.push(root);

				while (!dirs.isEmpty()) {
					File dir = dirs.pop();
					for (File app : dir.listFiles()) {
						if (app.isDirectory()) {
							dirs.push(app);
							continue;
						}

						if (!filteringFunction.apply(app.getAbsolutePath())) {
							continue;
						}

						String relativeDir = rootPath.relativize(dir.toPath()).toString();
						Path destinationPath = Paths.get(FileUtility.joinPath(destination.getAbsolutePath(), relativeDir, app.getName()));
						if (!destinationPath.getParent().toFile().exists()) {
							destinationPath.getParent().toFile().mkdirs();
						}
						Files.copy(app.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
						if (!postProcessingFunction.apply(destinationPath.toString())) {
				        	LOGGER.warning("Failed to apply post processing function to path " + destination);
				        }
					}
				}
			} catch (URISyntaxException ex) {
				// never happens
			}
		}
	}

	/**
	 * IO combining two paths
	 * @param path1 first path
	 * @param path2 second path
	 * @return a path created by joining first path and second path
	 */
	private static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	/**
	 * IO combining paths
	 * @param paths array of paths
	 * @return a path created by joining all the paths
	 */
	public static String joinPath(String... paths) {
		if (paths.length == 0) {
			return "";
		} else {
			String output = paths[0];
			for (int i = 1; i < paths.length; i++) {
				output = joinPath(output, paths[i]);
			}
			return output;
		}
	}
}