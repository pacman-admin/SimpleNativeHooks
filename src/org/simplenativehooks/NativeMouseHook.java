package org.simplenativehooks;

import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;

import java.util.logging.Logger;

public class NativeMouseHook extends AbstractGlobalMouseListener implements NativeHookMouseEventSubscriber {

    private static final Logger LOGGER = Logger.getLogger(NativeMouseHook.class.getName());

    private NativeMouseHook() {
    }

    public static NativeMouseHook of() {
        return new NativeMouseHook();
    }

    @Override
    public void startListening() {
        NativeHookGlobalEventPublisher.of().addMouseEventSubscriber(this);

    }

    @Override
    public void stopListening() {
        NativeHookGlobalEventPublisher.of().removeMouseEventSubscriber(this);

    }

    @Override
    public void processMouseEvent(NativeMouseEvent event) {
        if (event.getState().equals(NativeMouseEvent.State.MOVED)) {
            mouseMoved.apply(event);
        } else if (event.getState().equals(NativeMouseEvent.State.PRESSED)) {
            mousePressed.apply(event);
        } else if (event.getState().equals(NativeMouseEvent.State.RELEASED)) {
            mouseReleased.apply(event);
        } else { // Drop
            LOGGER.finer("Silently dropping mouse event with unknown state.");
        }
    }

}
