package org.simplenativehooks;

import java.util.function.Function;
import java.util.logging.Logger;

import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;

public final class NativeKeyHook extends AbstractGlobalKeyListener implements NativeHookKeyEventSubscriber {

	private static final Logger LOGGER = Logger.getLogger(NativeKeyHook.class.getName());

	private NativeKeyHook(Function<NativeKeyEvent, ?> press, Function<NativeKeyEvent, ?> release) {
		super(press, release);
	}

	public static NativeKeyHook of(Function<NativeKeyEvent, ?> press, Function<NativeKeyEvent, ?> release) {
        return new NativeKeyHook(press,release);
	}

	@Override
	public void startListening() {
		NativeHookGlobalEventPublisher.of().addKeyEventSubscriber(this);

	}

	@Override
	public void stopListening() {
		NativeHookGlobalEventPublisher.of().removeKeyEventSubscriber(this);
	}

	@Override
	public void processKeyboardEvent(NativeKeyEvent event) {
		if (event.isPressed()) {
			keyPressed.apply(event);
		} else {
			keyReleased.apply(event);
		}
	}

}
