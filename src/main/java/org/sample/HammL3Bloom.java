package org.sample;

import sun.misc.Unsafe;

final class HammL3Bloom {

    private static final Unsafe UNSAFE = HammL3Util.UNSAFE;

    private static final int SIZE_INITIAL = 256;

    private static final int SIZE_RATIO = 8;

    private static final byte[] BIT_MASKS = {
            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
            (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80
    };

    private int size;

    private int capacity;

    private long dataPtr;

    private int indexMask;

    public HammL3Bloom() {
        this.size = 0;
        this.capacity = 0;
        this.dataPtr = 0;
    }

    public void destroy() {
        if (this.dataPtr != 0) {
            UNSAFE.freeMemory(this.dataPtr);
            this.dataPtr = 0;
            this.capacity = 0;
        }
    }

    public void reallocate(int size) {
        if (this.dataPtr != 0) {
            UNSAFE.freeMemory(this.dataPtr);
            this.dataPtr = 0;
            this.capacity = 0;
        }

        this.size = 0;

        if (size < SIZE_INITIAL) {
            return;
        }

        // Округляем вверх до ближайшей степени двойки
        int capacity = size;
        capacity--;
        capacity |= capacity >> 1;
        capacity |= capacity >> 2;
        capacity |= capacity >> 4;
        capacity |= capacity >> 8;
        capacity |= capacity >> 16;
        capacity++;

        // Применяем масштабный коэффициент
        capacity >>= SIZE_RATIO;

        this.dataPtr = UNSAFE.allocateMemory(capacity);
        this.indexMask = capacity - 1;
        this.capacity = capacity;
    }

    public void append(long value) {
        if (capacity > 0) {
            long mix = value;
            mix ^= mix >> 15;
            mix ^= mix << 33;

            int index = (int) (mix & indexMask);
            int bit = (int) ((mix & 0x0000070000000000L) >> 40);

            long offset = this.dataPtr + index;
            byte b = UNSAFE.getByte(offset);
            b |= BIT_MASKS[bit];
            UNSAFE.putByte(offset, b);
        }

        size++;
    }

    public boolean contains(long value) {
        if (capacity > 0) {
            long mix = value;
            mix ^= mix >> 15;
            mix ^= mix << 33;

            int index = (int) (mix & indexMask);
            int bit = (int) ((mix & 0x0000070000000000L) >> 40);

            long offset = this.dataPtr + index;
            byte b = UNSAFE.getByte(offset);
            return (b & BIT_MASKS[bit]) != 0;
        } else {
            return true;
        }
    }

    public boolean isOverloaded() {
        if (capacity > 0) {
            return (size >> SIZE_RATIO) >= capacity;
        } else {
            return size >= SIZE_INITIAL;
        }
    }

}
