package org.simplenativehooks;

import org.simplenativehooks.utilities.FileUtility;
import org.simplenativehooks.utilities.OSIdentifier;
import org.simplenativehooks.utilities.Platform;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class BootstrapResources {
    private static final Logger LOGGER = Logger.getLogger(BootstrapResources.class.getName());

    public static void extract() throws IOException, URISyntaxException {
        new BootstrapResources().extractResources();
    }

    public static File getNativeHookDirectory() {
        return new File(FileUtility.joinPath("resources", "nativehooks", getOSDir()));
    }

    public static File getNativeHookExecutable() {
        String file = "";

        if (Platform.isWindows()) {
            file = "RepeatHook.exe";
        } else {
            file = "RepeatHook.out";
        }
        return new File(FileUtility.joinPath(getNativeHookDirectory().getAbsolutePath(), file));
    }

    private static String getOSDir() {
        switch (Platform.get()) {
            case WINDOWS -> {
                return "windows";
            }
            case OTHER -> {
                if (NativeHookInitializer.USE_X11_ON_LINUX) {
                    return "x11";
                } else {
                    return "linux";
                }
            }
            case MAC -> {
                return "osx";
            }
        }
        throw new IllegalStateException("Your OS is not supported.");
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
        if () {
            if (NativeHookInitializer.USE_X11_ON_LINUX) {
                if (name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out")) {
                    return new File(name).setExecutable(true);
                }
            } else if (name.endsWith("RepeatHook.out")) {
                return new File(name).setExecutable(true);
            }
        }
        if (Platform.isMac() && name.endsWith("RepeatHook.out")) {
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
