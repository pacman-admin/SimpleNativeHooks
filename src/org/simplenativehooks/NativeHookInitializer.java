package org.simplenativehooks;

import org.simplenativehooks.linux.GlobalLinuxEventOrchestrator;
import org.simplenativehooks.osx.GlobalOSXEventOrchestrator;
import org.simplenativehooks.windows.GlobalWindowsEventOrchestrator;
import org.simplenativehooks.x11.GlobalX11EventOrchestrator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeHookInitializer {

    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = Logger.getLogger(NativeHookInitializer.class.getName());
    private static ControlMode mode = ControlMode.AUTO;

    public static void start() {
        start(mode);
    }

    public static void start(ControlMode mode) {
        NativeHookInitializer.mode = mode;
        switch (mode) {
            case AUTO -> start(ControlMode.determine());
            case WINDOWS -> GlobalWindowsEventOrchestrator.of().start();
            case MAC -> GlobalOSXEventOrchestrator.of().start();
            case X11 -> GlobalX11EventOrchestrator.of().start();
            case UNIX -> GlobalLinuxEventOrchestrator.of().start();
        }
    }

    public static void stop(ControlMode mode) {
        NativeHookInitializer.mode = mode;
        switch (mode) {
            case AUTO -> stop(ControlMode.determine());
            case WINDOWS -> {
                try {
                    GlobalWindowsEventOrchestrator.of().stop();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
                }
            }
            case MAC -> {
                try {
                    GlobalOSXEventOrchestrator.of().stop();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
                }
            }
            case X11 -> GlobalX11EventOrchestrator.of().stop();
            case UNIX -> GlobalLinuxEventOrchestrator.of().stop();
        }
    }

    public static void stop() {
        stop(mode);
    }
}