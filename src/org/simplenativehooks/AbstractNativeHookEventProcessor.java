package org.simplenativehooks;

import org.simplenativehooks.utilities.StringUtil;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractNativeHookEventProcessor {
    private static final Logger LOGGER = Logger.getLogger(AbstractNativeHookEventProcessor.class.getName());
    private boolean withSudo; // Run as root.
    private Process process;
    private Thread stdoutThread, stderrThread;

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
        if (process != null || stdoutThread != null || stderrThread != null) {
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
        final String[] runningCommand = command;
        LOGGER.info(getName() + ": running command $" + StringUtil.join(command, " "));
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(executableDir);
            if (withSudo) {
                processBuilder.redirectInput(Redirect.INHERIT);
            }

            process = processBuilder.start();
            process.onExit().thenAccept(p -> System.out.println("Native hook process exited with status code: " + p.exitValue()));
            process.onExit().thenAccept(p -> reset());

            stdoutThread = new Thread(this::processStdout);
            stderrThread = new Thread(this::processStderr);
            stdoutThread.start();
            stderrThread.start();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception encountered while running command " + Arrays.toString(command), e);
            reset();
        }
    }

    public final void stop() throws InterruptedException {
        process.destroy();
        LOGGER.info("Native hook process for " + AbstractNativeHookEventProcessor.this.getName() + " destroyed.");
        process.destroyForcibly();
        if (process.isAlive()) {
            System.err.println("Cannot kill task");
        }
    }

    public final boolean isRunning() {
        if (process == null) {
            return false;
        }
        return process.isAlive();
    }

    private void processStdout() {
        try {
            String line;
            while ((line = process.inputReader().readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isBlank()) {
                    continue;
                }
                try {
                    processStdout(line);
                } catch (Throwable e) {
                    LOGGER.log(Level.WARNING, "Exception when processing stdout line for " + getName() + ". " + e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Exception when processing stdout for " + getName() + ". " + e.getMessage(), e);
        }
    }

    private void processStderr() {
        try {
            String line;
            while ((line = process.errorReader().readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                try {
                    processStderr(line);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Exception when processing stderr line for " + getName() + ". " + e.getMessage(), e);
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Exception when processing stderr for " + getName() + ". " + e.getMessage(), e);
        }

    }

    private void reset() {
        stdoutThread = null;
        stderrThread = null;
        if (process != null) {
            process.destroy();
            process.destroyForcibly();
        }
    }
}
