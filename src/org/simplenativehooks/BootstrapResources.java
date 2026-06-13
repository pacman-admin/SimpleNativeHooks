package org.simplenativehooks;

import org.simplenativehooks.utilities.FileUtil;
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
        return new File(FileUtil.joinPath("resources", "nativehooks", getOSDir()));
    }

    public static File getNativeHookExecutable() {
        String file;

        if (Platform.isWindows()) {
            file = "RepeatHook.exe";
        } else {
            file = "RepeatHook.out";
        }
        return new File(FileUtil.joinPath(getNativeHookDirectory().getAbsolutePath(), file));
    }

    private static String getOSDir() {
        return NativeHookInitializer.getMode().toString();
    }

    private void extractResources() throws IOException, URISyntaxException {
        if (!FileUtil.createDirectory(getExtractingDest().getAbsolutePath())) {
            LOGGER.warning("Failed to extract " + getName() + " resources");
            return;
        }

        final String path = getRelativeSourcePath();
        FileUtil.extractFromCurrentJar(path, getExtractingDest(), this::correctExtension, this::postProcessing);
    }

    private boolean postProcessing(String name) {
        if (Platform.isUnix()) {
            if (NativeHookInitializer.getMode() == ControlMode.X11) {
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
        if (Platform.isWindows()) {
            return name.endsWith("RepeatHook.exe");
        }
        if (Platform.isUnix()) {
            if (NativeHookInitializer.getMode() == ControlMode.X11) {
                return name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out");
            } else {
                return name.endsWith("RepeatHook.out");
            }
        }
        if (Platform.isMac()) {
            return name.endsWith("RepeatHook.out");
        }
        throw new IllegalStateException("OS is unsupported.");
    }


    private String getRelativeSourcePath() {
        return "org/simplenativehooks/" + getOSDir() + "/nativecontent";
    }

    private File getExtractingDest() {
        return new File(FileUtil.joinPath("resources", "nativehooks", getOSDir()));
    }

    private String getName() {
        return "NativeHook";
    }
}
