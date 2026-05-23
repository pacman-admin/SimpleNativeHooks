package org.simplenativehooks.events;

public interface NativeHookMouseEvent {
	NativeMouseEvent convertEvent() throws InvalidMouseEventException;
}
