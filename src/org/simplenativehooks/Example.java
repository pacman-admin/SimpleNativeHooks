package org.simplenativehooks;

import org.simplenativehooks.staticResources.BootStrapResources;

import java.io.IOException;
import java.net.URISyntaxException;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        /* Extracting resources */
        try {
            BootStrapResources.extractResources();
        } catch (IOException | URISyntaxException e) {
            System.out.println("Cannot extract bootstrap resources.");
            e.printStackTrace();
            System.exit(2);
        }
        /* Initializing global hooks */
        NativeHookInitializer.start();

        /* Set up callbacks */
        NativeKeyHook key = NativeKeyHook.of();
        key.setKeyPressed(d -> {
            System.out.println("Key pressed: " + d.getKey());
            return null;
        });
        key.setKeyReleased(d -> {
            System.out.println("Key released: " + d.getKey());
            return null;
        });
        key.startListening();

        NativeMouseHook mouse = NativeMouseHook.of();
        mouse.setMousePressed(d -> {
            System.out.println("Mouse pressed button " + d.getButton() + " at " + d.getX() + ", " + d.getY());
            return true;
        });
        mouse.setMouseReleased(d -> {
            System.out.println("Mouse released button " + d.getButton() + " at " + d.getX() + ", " + d.getY());
            return true;
        });
        mouse.setMouseMoved(d -> {
            System.out.println("Mouse moved to " + d.getX() + ", " + d.getY());
            return true;
        });
        mouse.startListening();

        /* Wait for testing before shutting down. */
        Thread.sleep(60000);

        /* Clean up */
        NativeHookInitializer.stop();
        mouse.stopListening();
        key.stopListening();
    }
}
