package org.simplenativehooks;

public class Example {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) throws InterruptedException {
        /* Initializing global hooks */
        NativeHookInitializer.start();

        /* Set up callbacks */
        NativeKeyHook key = NativeKeyHook.of(d -> {
            System.out.println("Key pressed: " + d.getKey());
            return true;
        }, d -> {
            System.out.println("Key released: " + d.getKey());
            return true;
        });
        key.startListening();

        NativeMouseHook mouse = NativeMouseHook.of(d -> {
            System.out.println("Mouse pressed button " + d.getButton() + " at " + d.getX() + ", " + d.getY());
            return true;
        }, d -> {
            System.out.println("Mouse released button " + d.getButton() + " at " + d.getX() + ", " + d.getY());
            return true;
        }, d -> {
            System.out.println("Mouse moved to " + d.getX() + ", " + d.getY());
            return true;
        });
        mouse.startListening();

        /* Wait for testing before shutting down. */
        Thread.sleep(30000);

        /* Clean up */
        NativeHookInitializer.stop();
        mouse.stopListening();
        key.stopListening();
        System.out.println("Exited");
        System.exit(0);
    }
}
