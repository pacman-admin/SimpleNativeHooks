package org.simplenativehooks.staticResources;

import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.utilities.FileUtility;
import org.simplenativehooks.utilities.OSIdentifier;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class NativeHookBootstrapResources {
    private static final Logger LOGGER = Logger.getLogger(NativeHookBootstrapResources.class.getName());

    public static void extract() throws IOException, URISyntaxException {
        new NativeHookBootstrapResources().extractResources();
    }

    public static File getNativeHookDirectory() {
        return new File(FileUtility.joinPath("resources", "nativehooks", getOSDir()));
    }

    public static File getNativeHookExecutable() {
        String file = "";

        if (OSIdentifier.IS_WINDOWS) {
            file = "RepeatHook.exe";
        }
        if (OSIdentifier.IS_LINUX) {
            file = "RepeatHook.out";
        }
        if (OSIdentifier.IS_OSX) {
            file = "RepeatHook.out";
        }
        return new File(FileUtility.joinPath(getNativeHookDirectory().getAbsolutePath(), file));
    }

    private static String getOSDir() {
        if (OSIdentifier.IS_WINDOWS) {
            return "windows";
        } else if (OSIdentifier.IS_LINUX) {
            if (NativeHookInitializer.USE_X11_ON_LINUX) {
                return "x11";
            } else {
                return "linux";
            }
        } else if (OSIdentifier.IS_OSX) {
            return "osx";
        }
        throw new IllegalStateException("OS is unsupported.");
    }

    private void extractResources() throws IOException, URISyntaxException {
        if (!FileUtility.createDirectory(getExtractingDest().getAbsolutePath())) {
            LOGGER.warning("Failed to extract " + getName() + " resources");
            return;
        }

        final String path = getRelativeSourcePath();
        FileUtility.extractFromCurrentJar(path, getExtractingDest(), this::correctExtension, this::postProcessing);
    }

    private boolean postProcessing(String name) {
        if (OSIdentifier.IS_LINUX) {
            if (NativeHookInitializer.USE_X11_ON_LINUX) {
                if (name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out")) {
                    return new File(name).setExecutable(true);
                }
            } else if (name.endsWith("RepeatHook.out")) {
                return new File(name).setExecutable(true);
            }
        }
        if (OSIdentifier.IS_OSX && name.endsWith("RepeatHook.out")) {
            return new File(name).setExecutable(true);
        }
        return true;
    }


    private boolean correctExtension(String name) {
        if (OSIdentifier.IS_WINDOWS) {
            return name.endsWith("RepeatHook.exe");
        }
        if (OSIdentifier.IS_LINUX) {
            if (NativeHookInitializer.USE_X11_ON_LINUX) {
                return name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out");
            } else {
                return name.endsWith("RepeatHook.out");
            }
        }
        if (OSIdentifier.IS_OSX) {
            return name.endsWith("RepeatHook.out");
        }
        throw new IllegalStateException("OS is unsupported.");
    }


    private String getRelativeSourcePath() {
        return "org/simplenativehooks/" + getOSDir() + "/nativecontent";
    }

    private File getExtractingDest() {
        return new File(FileUtility.joinPath("resources", "nativehooks", getOSDir()));
    }

    private String getName() {
        return "NativeHook";
    }
}
