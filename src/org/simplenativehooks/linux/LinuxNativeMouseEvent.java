package org.simplenativehooks.linux;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.InputEvent;

import org.simplenativehooks.events.InvalidMouseEventException;
import org.simplenativehooks.events.NativeHookMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent.State;

class LinuxNativeMouseEvent implements NativeHookMouseEvent {
	private final int type;
	private final int code;
	private final int value;

	private LinuxNativeMouseEvent(int type, int code, int value) {
		this.type = type;
		this.code = code;
		this.value = value;
	}

	protected static LinuxNativeMouseEvent of(int type, int code, int value) {
		return new LinuxNativeMouseEvent(type, code, value);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		State s = State.UNKNOWN;
		int button = 0;
		Point p;

		switch (type) {
		case 1: // EV_KEY --> button click.
            button = switch (code) {
                case 0x110 -> // BTN_MOUSE, also BTN_LEFT
                        InputEvent.BUTTON1_DOWN_MASK;
                case 0x111 -> // BTN_RIGHT
                        InputEvent.BUTTON3_DOWN_MASK;
                case 0x112 -> // BTN_MIDDLE
                        InputEvent.BUTTON2_DOWN_MASK;
                default ->
                        throw new InvalidMouseEventException("Unknown code '" + code + "' for button click on mouse.");
            };

            s = switch (value) {
                case 0 -> // Released.
                        State.RELEASED;
                case 1 -> // Pressed.
                        State.PRESSED;
                default ->
                        throw new InvalidMouseEventException("Unknown value '" + value + "' for button click on mouse.");
            };

			p = MouseInfo.getPointerInfo().getLocation();
			return NativeMouseEvent.of(p.x, p.y, s, button);
		case 2: // EV_REL --> mouse moved or scrolled.
            // REL_WHEEL
            return switch (code) { // REL_X
                case 0x01, 0x02 -> // REL_Y
                        currentMovedPosition(button); // REL_HWHEEL
                case 0x06, 0x08 -> throw new InvalidMouseEventException("Not handling scrolling events.");
                default -> throw new InvalidMouseEventException("Unknown code '" + code + "' for type '" + type + "'.");
            };
		case 3: // EV_ABS --> mouse moved.
            return switch (code) {
                case 0x00 -> // ABS_X
                        currentMovedPosition(button);
                case 0x01 -> // ABS_Y
                        currentMovedPosition(button);
                default -> throw new InvalidMouseEventException("Unknown code '" + code + "' for type '" + type + "'.");
            };
		default:
			throw new InvalidMouseEventException("Unknown type '" + type + ".");
		}
	}

	private NativeMouseEvent currentMovedPosition(int button) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		return NativeMouseEvent.of(p.x, p.y, State.MOVED, button);
	}
}
