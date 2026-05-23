package org.simplenativehooks.events;

public interface NativeHookKeyEvent {
	NativeKeyEvent convertEvent() throws InvalidKeyEventException;
}
