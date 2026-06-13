package org.simplenativehooks;

import java.util.function.Function;
import java.util.logging.Logger;

import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;

public final class NativeKeyHook extends AbstractGlobalKeyListener implements NativeHookKeyEventSubscriber {

	private static final Logger LOGGER = Logger.getLogger(NativeKeyHook.class.getName());
	private NativeKeyHook() {}

	public static NativeKeyHook of() {
		return new NativeKeyHook();
	}
	public static NativeKeyHook of(Function<NativeKeyEvent, ?> press, Function<NativeKeyEvent, ?> release) {
		NativeKeyHook hook = new NativeKeyHook();
		hook.setKeyPressed(press);
		hook.setKeyReleased(release);
		return hook;
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
