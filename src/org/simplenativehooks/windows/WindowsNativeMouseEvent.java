package org.simplenativehooks.windows;

import java.awt.event.InputEvent;

import org.simplenativehooks.events.InvalidMouseEventException;
import org.simplenativehooks.events.NativeHookMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent.State;

class WindowsNativeMouseEvent implements NativeHookMouseEvent {
	private final int x;
	private final int y;

	private final int code;

	private WindowsNativeMouseEvent(int x, int y, int code) {
		this.x = x;
		this.y = y;
		this.code = code;
	}

	protected static WindowsNativeMouseEvent of(int x, int y, int code) {
		return new WindowsNativeMouseEvent(x, y, code);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		State s;
		int button = switch (code) {
            case 512 -> {
                s = State.MOVED;
                yield 0;
            }
            case 513 -> {
                s = State.PRESSED;
                yield InputEvent.BUTTON1_DOWN_MASK;
            }
            case 514 -> {
                s = State.RELEASED;
                yield InputEvent.BUTTON1_DOWN_MASK;
            }
            case 516 -> {
                s = State.PRESSED;
                yield InputEvent.BUTTON3_DOWN_MASK;
            }
            case 517 -> {
                s = State.RELEASED;
                yield InputEvent.BUTTON3_DOWN_MASK;
            }
            case 519 -> {
                s = State.PRESSED;
                yield InputEvent.BUTTON2_DOWN_MASK;
            }
            case 520 -> {
                s = State.RELEASED;
                yield InputEvent.BUTTON2_DOWN_MASK;
            }
            case 522 -> {
                s = State.SCROLLED;
                yield 0;
            }
            default -> throw new InvalidMouseEventException("Unknown code " + code + ".");
        };

        return NativeMouseEvent.of(x, y, s, button);
	}
}
