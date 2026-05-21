package org.simplenativehooks;

import org.simplenativehooks.linux.GlobalLinuxEventOchestrator;
import org.simplenativehooks.osx.GlobalOSXEventOchestrator;
import org.simplenativehooks.utilities.OSIdentifier;
import org.simplenativehooks.windows.GlobalWindowsEventOchestrator;
import org.simplenativehooks.x11.GlobalX11EventOchestrator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeHookInitializer {

    public static final String VERSION = "1.0.0";
    public static final boolean USE_X11_ON_LINUX = true;
    private static final Logger LOGGER = Logger.getLogger(NativeHookInitializer.class.getName());
    public void start() {
        if (OSIdentifier.IS_WINDOWS) {
            GlobalWindowsEventOchestrator.of().start();
            return;
        }
        if (OSIdentifier.IS_LINUX) {
            if (USE_X11_ON_LINUX) {
                GlobalX11EventOchestrator.of().start();
            } else {
                GlobalLinuxEventOchestrator.of().start();
            }
            return;
        }
        if (OSIdentifier.IS_OSX) {
            GlobalOSXEventOchestrator.of().start();
            return;
        }

        throw new RuntimeException("OS not supported.");
    }

    public void stop() {
        if (OSIdentifier.IS_WINDOWS) {
            try {
                GlobalWindowsEventOchestrator.of().stop();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
            }
            return;
        }
        if (OSIdentifier.IS_LINUX) {
            if (USE_X11_ON_LINUX) {
                GlobalX11EventOchestrator.of().stop();
            } else {
                GlobalLinuxEventOchestrator.of().stop();
            }
            return;
        }
        if (OSIdentifier.IS_OSX) {
            try {
                GlobalOSXEventOchestrator.of().stop();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
            }
            return;
        }

        throw new RuntimeException("OS not supported.");
    }
}
