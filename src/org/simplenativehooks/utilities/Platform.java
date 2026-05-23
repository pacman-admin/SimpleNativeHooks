/*
 * Copyright (c) 2026 Langdon Staab <langdon@langdonstaab.ca>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.simplenativehooks.utilities;

public enum Platform {
    WINDOWS, MAC, OTHER;
    private static final Platform PLATFORM = determinePlatform();

    private static Platform determinePlatform() {
        String s = System.getProperty("os.name").toLowerCase();
        if (s.startsWith("win")) {
            return WINDOWS;
        }
        if (s.startsWith("mac")) {
            return MAC;
        }
        return OTHER;
    }

    public static Platform get() {
        return PLATFORM;
    }

    public static boolean isWindows() {
        return PLATFORM == WINDOWS;
    }

    /**
     * Determine if the platform this program is running on is some kind of Unix or Unix-like system.
     * @return If the current OS is neither Windows nor macOS
     */
    public static boolean isUnix() {
        return PLATFORM == OTHER;
    }

    public static boolean isMac() {
        return PLATFORM == MAC;
    }
}
