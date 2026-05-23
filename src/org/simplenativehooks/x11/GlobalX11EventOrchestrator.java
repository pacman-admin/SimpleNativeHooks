package org.simplenativehooks.x11;

import java.io.File;
import java.util.logging.Logger;

import org.simplenativehooks.staticResources.NativeHookBootstrapResources;

public class GlobalX11EventOrchestrator {

	private static final Logger LOGGER = Logger.getLogger(GlobalX11EventOrchestrator.class.getName());

	private static final String MOUSE_EXECUTABLE = "RepeatHookX11Mouse.out";
	private static final String KEYBOARD_EXECUTABLE = "RepeatHookX11Key.out";

	private static final GlobalX11EventOrchestrator INSTANCE = new GlobalX11EventOrchestrator();

	private final X11NativeEventProcessor mouse;
	private final X11NativeEventProcessor keyboard;

	private GlobalX11EventOrchestrator() {
		File dir = NativeHookBootstrapResources.getNativeHookDirectory();

		mouse = X11NativeEventProcessor.of(new File(dir.getAbsolutePath(), MOUSE_EXECUTABLE));
		keyboard = X11NativeEventProcessor.of(new File(dir.getAbsolutePath(), KEYBOARD_EXECUTABLE));
	}

	public static GlobalX11EventOrchestrator of() {
		return INSTANCE;
	}

	public final void start() {
		mouse.start();
		keyboard.start();
	}

	public final void stop() {
		try {
			mouse.stop();
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while waiting for mouse process to end.");
		}
		try {
			keyboard.stop();
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while waiting for keyboard process to end.");
		}
	}
}
