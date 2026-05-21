package org.simplenativehooks;

import org.simplenativehooks.events.NativeMouseEvent;

public interface NativeHookMouseEventSubscriber {
	void processMouseEvent(NativeMouseEvent event);
}
