package org.simplenativehooks.listeners;

import org.simplenativehooks.events.NativeMouseEvent;

import java.util.function.Function;

public abstract class AbstractGlobalMouseListener implements GlobalListener {

	protected Function<NativeMouseEvent, ?> mousePressed;
	protected Function<NativeMouseEvent, ?> mouseReleased;
	protected Function<NativeMouseEvent, ?> mouseMoved;

	protected AbstractGlobalMouseListener() {}

	public final void setMousePressed(Function<NativeMouseEvent, ?> mousePressed) {
		this.mousePressed = mousePressed;
	}
	public final void setMouseReleased(Function<NativeMouseEvent, ?> mouseReleased) {
		this.mouseReleased = mouseReleased;
	}
	public final void setMouseMoved(Function<NativeMouseEvent, ?> mouseMoved) {
		this.mouseMoved = mouseMoved;
	}
}
