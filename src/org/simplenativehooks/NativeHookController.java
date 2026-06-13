package org.simplenativehooks;

import org.simplenativehooks.linux.GlobalLinuxEventOrchestrator;
import org.simplenativehooks.osx.GlobalOSXEventOrchestrator;
import org.simplenativehooks.windows.GlobalWindowsEventOrchestrator;
import org.simplenativehooks.x11.GlobalX11EventOrchestrator;

import java.util.logging.Level;

public class NativeHookController {
    public final ControlMode mode;
    public NativeHookController() {
        this(ControlMode.determine());
    }

    public NativeHookController(ControlMode mode) {
        if (mode == ControlMode.AUTO) {
            this.mode = ControlMode.determine();
        } else {
            this.mode = mode;
        }
    }
    public void start() {
        switch (mode) {
            case WINDOWS -> GlobalWindowsEventOrchestrator.of().start();
            case MAC -> GlobalOSXEventOrchestrator.of().start();
            case X11 -> GlobalX11EventOrchestrator.of().start();
            case UNIX -> GlobalLinuxEventOrchestrator.of().start();
        }
    }
    public void stop() {
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
}
