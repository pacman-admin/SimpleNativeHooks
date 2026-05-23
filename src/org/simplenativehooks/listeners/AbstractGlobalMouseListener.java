package org.simplenativehooks.listeners;

import org.simplenativehooks.events.NativeMouseEvent;

import java.util.function.Function;

public abstract class AbstractGlobalMouseListener implements GlobalListener {

	protected Function<NativeMouseEvent, Boolean> mousePressed;
	protected Function<NativeMouseEvent, Boolean> mouseReleased;
	protected Function<NativeMouseEvent, Boolean> mouseMoved;

	protected AbstractGlobalMouseListener() {
		mousePressed = d -> null;
		mouseReleased = d -> null;
		mouseMoved = d -> null;
	}

	public final void setMousePressed(Function<NativeMouseEvent, Boolean> mousePressed) {
		this.mousePressed = mousePressed;
	}
	public final void setMouseReleased(Function<NativeMouseEvent, Boolean> mouseReleased) {
		this.mouseReleased = mouseReleased;
	}
	public final void setMouseMoved(Function<NativeMouseEvent, Boolean> mouseMoved) {
		this.mouseMoved = mouseMoved;
	}
}
