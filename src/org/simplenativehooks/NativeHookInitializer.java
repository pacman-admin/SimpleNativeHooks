package org.simplenativehooks;

import org.simplenativehooks.linux.GlobalLinuxEventOrchestrator;
import org.simplenativehooks.osx.GlobalOSXEventOrchestrator;
import org.simplenativehooks.utilities.OSIdentifier;
import org.simplenativehooks.windows.GlobalWindowsEventOrchestrator;
import org.simplenativehooks.x11.GlobalX11EventOrchestrator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeHookInitializer {

    public static final String VERSION = "1.0.0";
    public static boolean USE_X11_ON_LINUX = true;
    private static final Logger LOGGER = Logger.getLogger(NativeHookInitializer.class.getName());
    public static void start() {
        String windowEnv = System.getenv("XDG_SESSION_TYPE");
        if (windowEnv == null) return;
        if (windowEnv.equalsIgnoreCase("Wayland")) {
            USE_X11_ON_LINUX = false;
//            LOGGER.warning("Your computer is running Wayland.\nRepeat will not be able to control mouse position.\nRecording and replaying of actions will only work in an X window.");
        }
        if (OSIdentifier.IS_WINDOWS) {
            GlobalWindowsEventOrchestrator.of().start();
            return;
        }
        if (OSIdentifier.IS_LINUX) {
            if (USE_X11_ON_LINUX) {
                GlobalX11EventOrchestrator.of().start();
            } else {
                GlobalLinuxEventOrchestrator.of().start();
            }
            return;
        }
        if (OSIdentifier.IS_OSX) {
            GlobalOSXEventOrchestrator.of().start();
            return;
        }

        throw new RuntimeException("OS not supported.");
    }

    public static void stop() {
        if (OSIdentifier.IS_WINDOWS) {
            try {
                GlobalWindowsEventOrchestrator.of().stop();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
            }
            return;
        }
        if (OSIdentifier.IS_LINUX) {
            if (USE_X11_ON_LINUX) {
                GlobalX11EventOrchestrator.of().stop();
            } else {
                GlobalLinuxEventOrchestrator.of().stop();
            }
            return;
        }
        if (OSIdentifier.IS_OSX) {
            try {
                GlobalOSXEventOrchestrator.of().stop();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
            }
            return;
        }

        throw new RuntimeException("OS not supported.");
    }
}
