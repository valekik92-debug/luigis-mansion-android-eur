package org.libsdl.app;

/**
 * Reads the game's own state (read only) through the native helper (liblmmods).
 *
 * EUR fork: addresses are the European (GLMP01) ones, mapped from the USA ones with the recompiler's
 * version_addrs.inc (USA lbl_804CFFF0 -> PAL lbl_804BD6B8).
 *
 * Game Boy Horror search view: the game's view mode word (USA 0x804CFFF0) is 2 while the
 * Game Boy Horror's first-person search view is up (engine source, coop_fixes.cpp kGbhMode; the same
 * address in Premium Deluxe, whose r13 is also 0x804D7F60). In that view the walking stick looks
 * around with up and down the other way round, so the port turns the stick's Y over while it is up.
 */
final class GuestMem {
    private GuestMem() { }

    static { try { System.loadLibrary("lmmods"); } catch (Throwable ignored) { } }

    static native long nativeRead32(int addr);
    static native int nativeWrite16(int addr, int value);

    private static final int GBH_MODE = 0x804BD6B8;  // EUR (GLMP01); USA 0x804CFFF0
    private static volatile long checkedAt;
    private static volatile boolean gbh;

    /** True while the Game Boy Horror search view is up (checked at most every 50 ms). */
    static boolean gbhView() {
        long now = android.os.SystemClock.uptimeMillis();
        if (now - checkedAt >= 50) {
            checkedAt = now;
            long v;
            try { v = nativeRead32(GBH_MODE); } catch (Throwable t) { v = -1; }
            gbh = v == 2;
        }
        return gbh;
    }

    /** Left (walking) stick Y as the game should see it: turned over in the Game Boy Horror view. */
    static float leftStickY(float value) {
        return gbhView() ? -value : value;
    }
}
