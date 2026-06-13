package org.simplenativehooks.listeners;

import org.simplenativehooks.events.NativeKeyEvent;

import java.util.function.Function;

public abstract class AbstractGlobalKeyListener implements GlobalListener {
	protected Function<NativeKeyEvent, ?> keyPressed;
	protected Function<NativeKeyEvent, ?> keyReleased;
	protected AbstractGlobalKeyListener(Function<NativeKeyEvent, ?> press, Function<NativeKeyEvent, ?> release) {
		keyPressed = press;
        keyReleased = release;
	}

	public final void setKeyPressed(Function<NativeKeyEvent, ?> keyPressed) {
		this.keyPressed = keyPressed;
	}

	public final void setKeyReleased(Function<NativeKeyEvent, ?> keyReleased) {
		this.keyReleased = keyReleased;
	}
}
