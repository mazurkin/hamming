package org.sample;

final class HammL3Context {

    final long value;

    final int distance;

    final int bitCount;

    final int bitCount1;

    final int bitCount2;

    final int bitCount3;

    final int bitCount4;

    final int bitCountMin;

    final int bitCountMax;

    final int indexMin;

    final int indexMax;

    public HammL3Context(long value, int distance) {
        this.value = value;
        this.distance = distance;

        // Общее число бит в числе
        this.bitCount = Long.bitCount(value);

        // Минимально возможное общее число бит у искомого числа с учетом дистанции
        this.bitCountMin = Math.max(bitCount - distance, 0);

        // Максимально возможное общее число бит у искомого числа с учетом дистанции
        this.bitCountMax = Math.min(bitCount + distance, 64);

        // Секционные счетчики бит
        long v = value;
        this.bitCount1 = Integer.bitCount((int) v & 0xFFFF);
        v >>= 16;
        this.bitCount2 = Integer.bitCount((int) v & 0xFFFF);
        v >>= 16;
        this.bitCount3 = Integer.bitCount((int) v & 0xFFFF);
        v >>= 16;
        this.bitCount4 = Integer.bitCount((int) v & 0xFFFF);

        // Минимально возможный индекс по дистанции d для индекса [A][B][C][D]@x17 будет [A-d][B-d][C-d][D-d]@x17
        // (учет границы в 0 бит). Можно оптимизировать еще и эту оценку - распределением дистанции d по секциям.
        this.indexMin = calculateIndexMin(bitCount1, bitCount2, bitCount3, bitCount4, distance);

        // Максимально возможный индекс по дистанции d для индекса [A][B][C][D]@x17 будет [A+d][B+d][C+d][D+d]@x17
        // (учет границы в 16 бит). Можно оптимизировать еще и эту оценку - распределением дистанции d по секциям.
        this.indexMax = calculateIndexMax(bitCount1, bitCount2, bitCount3, bitCount4, distance);
    }

    private int calculateIndexMin(int bitCount1, int bitCount2, int bitCount3, int bitCount4, int distance) {
        // Распределяем дистанцию d от старших секций к младшим. В цикл не оформляем

        if (distance > 0) {
            if (bitCount4 >= distance) {
                bitCount4 -= distance;
                distance = 0;
            } else {
                distance -= bitCount4;
                bitCount4 = 0;
            }
        }

        if (distance > 0) {
            if (bitCount3 >= distance) {
                bitCount3 -= distance;
                distance = 0;
            } else {
                distance -= bitCount3;
                bitCount3 = 0;
            }
        }

        if (distance > 0) {
            if (bitCount2 >= distance) {
                bitCount2 -= distance;
                distance = 0;
            } else {
                distance -= bitCount2;
                bitCount2 = 0;
            }
        }

        if (distance > 0) {
            if (bitCount1 >= distance) {
                bitCount1 -= distance;
            } else {
                bitCount1 = 0;
            }
        }

        return
            bitCount1 /* 17^0 */ +
            bitCount2 /* 17^1 */ * 17 +
            bitCount3 /* 17^2 */ * 289 +
            bitCount4 /* 17^3 */ * 4913;
    }

    private int calculateIndexMax(int bitCount1, int bitCount2, int bitCount3, int bitCount4, int distance) {
        // Распределяем дистанцию d от старших секций к младшим. В цикл не оформляем

        if (distance > 0) {
            int room = 16 - bitCount4;
            if (room >= distance) {
                bitCount4 += distance;
                distance = 0;
            } else {
                distance -= room;
                bitCount4 = 16;
            }
        }

        if (distance > 0) {
            int room = 16 - bitCount3;
            if (room >= distance) {
                bitCount3 += distance;
                distance = 0;
            } else {
                distance -= room;
                bitCount3 = 16;
            }
        }

        if (distance > 0) {
            int room = 16 - bitCount2;
            if (room >= distance) {
                bitCount2 += distance;
                distance = 0;
            } else {
                distance -= room;
                bitCount2 = 16;
            }
        }

        if (distance > 0) {
            int room = 16 - bitCount1;
            if (room >= distance) {
                bitCount1 += distance;
            } else {
                bitCount1 = 16;
            }
        }

        return
            bitCount1 /* 17^0 */ +
            bitCount2 /* 17^1 */ * 17 +
            bitCount3 /* 17^2 */ * 289 +
            bitCount4 /* 17^3 */ * 4913;
    }

}
