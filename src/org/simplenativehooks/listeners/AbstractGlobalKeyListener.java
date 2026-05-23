package org.simplenativehooks.listeners;

import org.simplenativehooks.events.NativeKeyEvent;

import java.util.function.Function;

public abstract class AbstractGlobalKeyListener implements GlobalListener {
	protected Function<NativeKeyEvent, Boolean> keyPressed;
	protected Function<NativeKeyEvent, Boolean> keyReleased;

	protected AbstractGlobalKeyListener() {
		keyPressed = d -> null;
        keyReleased = d -> null;
	}

	public final void setKeyPressed(Function<NativeKeyEvent, Boolean> keyPressed) {
		this.keyPressed = keyPressed;
	}

	public final void setKeyReleased(Function<NativeKeyEvent, Boolean> keyReleased) {
		this.keyReleased = keyReleased;
	}
}
