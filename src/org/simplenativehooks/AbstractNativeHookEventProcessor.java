package org.simplenativehooks;

import org.simplenativehooks.utilities.StringUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractNativeHookEventProcessor {
    private static final Logger LOGGER = Logger.getLogger(AbstractNativeHookEventProcessor.class.getName());
    private static final long TIMEOUT_MS = 2000;

    private boolean withSudo; // Run as root.
    private Process process;
    private Set<Future<Void>> readers;

    public void setRunWithSudo() {
        this.withSudo = true;
    }

    @SuppressWarnings("unused")
    public void setRunWithoutSudo() {
        this.withSudo = false;
    }

    public abstract String getName();

    public abstract File getExecutionDir();

    public abstract String[] getCommand();

    public abstract void processStdout(String line);

    public abstract void processStderr(String line);

    public final void start() {
        if (process != null) {
            LOGGER.warning("Hook is already running...");
            return;
        }
        File executableDir = getExecutionDir();
        if (!executableDir.isDirectory()) {
            LOGGER.warning(getName() + " executable directory " + getExecutionDir().getAbsolutePath() + " does not exist or is not a directory.");
            return;
        }

        String[] command = getCommand();
        if (withSudo) {
            LOGGER.info("The pkexec command must exist on your system to authenticate the program for operation.");
            String[] commandWithSudo = new String[command.length + 1];
            System.arraycopy(command, 0, commandWithSudo, 1, command.length);
            commandWithSudo[0] = "pkexec";
            command = commandWithSudo;
        }
        LOGGER.info(getName() + ": running command $" + StringUtil.join(command, " "));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(executableDir);
            if (withSudo) {
                processBuilder.redirectInput(Redirect.INHERIT);
            }
            readers = Set.of(CompletableFuture.runAsync(this::processStderr), CompletableFuture.runAsync(this::processStdout));
            process = processBuilder.start();
            process.onExit().thenAccept(p -> System.out.println("Native hook process exited with status code: " + p.exitValue()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception encountered while running command " + Arrays.toString(command), e);
            reset();
        }
    }

    public final void stop() throws InterruptedException {
        process.destroy();
        readers.forEach(future -> future.cancel(true));
        readers.forEach(future -> System.out.println("Reader is cancelled?" + future.isDone()));
        process.waitFor();
    }

    public final boolean isRunning() {
        if (process == null) {
            return false;
        }
        return process.isAlive();
    }

    private void processStdout() {
        String line;
        while (true) {
            try {
                if ((line = process.inputReader().readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                processStdout(line);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception when processing stdout for " + getName() + ". " + e.getMessage(), e);
            }
        }
    }

    private void processStderr() {
        String line;
        while (true) {
            try {
                if ((line = process.errorReader().readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            try {
                processStderr(line);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception when processing stderr for " + getName() + ". " + e.getMessage(), e);
            }
        }
    }

    private void reset() {
        process = null;
        readers.forEach(future -> future.cancel(true));
        readers.clear();
    }
}
