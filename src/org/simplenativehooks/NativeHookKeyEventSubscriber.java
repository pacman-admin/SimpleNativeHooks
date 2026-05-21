package org.simplenativehooks;

import org.simplenativehooks.events.NativeKeyEvent;

public interface NativeHookKeyEventSubscriber {
	void processKeyboardEvent(NativeKeyEvent event);
}
