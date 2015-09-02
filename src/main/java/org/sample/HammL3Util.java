package org.sample;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

final class HammL3Util {

    public static final Unsafe UNSAFE = getUnsafe();

    public static final long[] FACTORIALS = {
            1L,
            1L,
            2L,
            6L,
            24L,
            120L,
            720L,
            5040L,
            40320L,
            362880L,
            3628800L,
            39916800L,
            479001600L,
            6227020800L,
            87178291200L,
            1307674368000L,
            20922789888000L
    };

    private HammL3Util() {
    }

    public static long C4(int k, int n) {
        return FACTORIALS[n] / FACTORIALS[k] / FACTORIALS[n - k];
    }

    public static int getIndex(long value) {
        int index = 0;

        index += Integer.bitCount((int) value & 0xFFFF) /* 17^0 */ ;
        value >>= 16;
        index += Integer.bitCount((int) value & 0xFFFF) /* 17^1 */ * 17;
        value >>= 16;
        index += Integer.bitCount((int) value & 0xFFFF) /* 17^2 */ * 289;
        value >>= 16;
        index += Integer.bitCount((int) value & 0xFFFF) /* 17^3 */ * 4913;

        return index;
    }

    @SuppressWarnings("restriction")
    private static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            throw new IllegalStateException("Fail to get the Unsafe instance");
        }
    }
}
