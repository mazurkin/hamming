package org.sample;

import sun.misc.Unsafe;

final class HammL3Cell {

    private static final int CAPACITY_INIT_MIN = 4;

    private static final int CAPACITY_INIT_MAX = 64 * 1024;

    private static final long CAPACITY_DECIMATOR = 27435582641610000L / CAPACITY_INIT_MAX;

    private static final int VALUE_SIZE = 8;

    private static final int PAYLOAD_SIZE = 8;

    private static final Unsafe UNSAFE = HammL3Util.UNSAFE;

    private final HammL3Stat stat;

    private long valuesPtr;

    private long payloadsPtr;

    private int capacity;

    private volatile int size;

    public HammL3Cell(int index, HammL3Stat stat) {
        this.size = 0;
        this.stat = stat;

        // Вычисляем секционные счетчики битов по индексу
        int i = index;
        int bitCount0 = i % 17;
        i /= 17;
        int bitCount1 = i % 17;
        i /= 17;
        int bitCount2 = i % 17;
        i /= 17;
        int bitCount3 = i % 17;

        // Число возможных вариантов long-числа теоретически попадающих под данный индекс рассчитывается как
        //
        // Q(index) = C(bitCount0,16) * C(bitCount1,16) * C(bitCount2,16) * C(bitCount3,16)
        // где С(k,n) = n! / k! / (n-k)!
        //
        // где максимальный вариант при bitCount0=bitCount1=bitCount2=bitCount3=8 будет равен
        // Q(8888@x17) = Q(41760@x10) = 12870 ^ 4 = 27435582641610000
        //
        // Поскольку принять вообще все теоретически возможные варианты у нас нет ни цели ни возможности нам
        // нужно просто выделить пропорциональный размер под массив. Если мы хотим хранить не более 16k элементов в
        // массиве вычисленный реальный размер нужно поделить на делитель:
        //      DECIMATOR = 27435582641610000 / 16384 = 1674535073340

        // Окончательно вычисляем емкость ячейки с учетом лимитов
        int capacity = (int) (
                HammL3Util.C4(bitCount0, 16)
                * HammL3Util.C4(bitCount1, 16)
                * HammL3Util.C4(bitCount2, 16)
                * HammL3Util.C4(bitCount3, 16)
                / CAPACITY_DECIMATOR
        );
        capacity = Math.max(CAPACITY_INIT_MIN, capacity);
        capacity = Math.min(CAPACITY_INIT_MAX, capacity);
        this.capacity = capacity;

        // Выделяем массивы - под хэши и полезную нагрузку
        this.valuesPtr = UNSAFE.allocateMemory(capacity * VALUE_SIZE);
        this.payloadsPtr = UNSAFE.allocateMemory(capacity * PAYLOAD_SIZE);

        // Статистика
        this.stat.capacity.addAndGet(capacity);
    }

    public synchronized void destroy() {
        UNSAFE.freeMemory(this.valuesPtr);
        UNSAFE.freeMemory(this.payloadsPtr);

        this.valuesPtr = 0;
        this.payloadsPtr = 0;
    }

    public synchronized void add(long value) {
        if (size == capacity) {
            int newCapacity = capacity * 2;

            this.stat.capacity.addAndGet(+newCapacity);
            this.stat.capacity.addAndGet(-capacity);
            this.stat.reallocs.incrementAndGet();
            this.stat.moves.addAndGet(+size);

            long newValuesPtr = UNSAFE.allocateMemory(newCapacity * VALUE_SIZE);
            long newPayloadsPtr = UNSAFE.allocateMemory(newCapacity * PAYLOAD_SIZE);

            UNSAFE.copyMemory(this.valuesPtr, newValuesPtr, size * VALUE_SIZE);
            UNSAFE.copyMemory(this.payloadsPtr, newPayloadsPtr, size * PAYLOAD_SIZE);

            UNSAFE.freeMemory(this.valuesPtr);
            UNSAFE.freeMemory(this.payloadsPtr);

            this.valuesPtr = newValuesPtr;
            this.payloadsPtr = newPayloadsPtr;

            this.capacity = newCapacity;
        }

        long valuesRunner = this.valuesPtr;
        for (int i = 0; i < size; i++) {
            if (UNSAFE.getLong(valuesRunner) == value) {
                return;
            }

            valuesRunner += VALUE_SIZE;
        }

        UNSAFE.putLong(this.valuesPtr + size * VALUE_SIZE, value);
        UNSAFE.putLong(this.payloadsPtr + size * PAYLOAD_SIZE, 0);

        size++;
    }

    public synchronized void remove(long value) {
        long valuesRunner = valuesPtr;
        for (int i = 0; i < size; i++) {
            if (UNSAFE.getLong(valuesRunner) == value) {
                if (i < size - 1) {
                    UNSAFE.copyMemory(
                            this.valuesPtr + (size - 1) * VALUE_SIZE,
                            this.valuesPtr + i * VALUE_SIZE,
                            VALUE_SIZE);
                    UNSAFE.copyMemory(
                            this.payloadsPtr + (size - 1) * PAYLOAD_SIZE,
                            this.payloadsPtr + i * PAYLOAD_SIZE,
                            PAYLOAD_SIZE);
                }

                size--;

                break;
            }

            valuesRunner += VALUE_SIZE;
        }
    }

    public synchronized boolean contains(long value, int distance) {
        long valuesRunner = this.valuesPtr;
        for (int i = 0; i < size; i++) {
            long xor = value ^ UNSAFE.getLong(valuesRunner);

            int bitCount = Long.bitCount(xor);

            if (bitCount <= distance) {
                return true;
            }

            valuesRunner += VALUE_SIZE;
        }

        return false;
    }

    public synchronized boolean contains(long value) {
        long valuesRunner = this.valuesPtr;
        for (int i = 0; i < size; i++) {
            if (value == UNSAFE.getLong(valuesRunner)) {
                return true;
            }

            valuesRunner += VALUE_SIZE;
        }

        return false;
    }

    public synchronized int count(long value, int distance) {
        int counter = 0;

        long valuesRunner = this.valuesPtr;
        for (int i = 0; i < size; i++) {
            long xor = value ^ UNSAFE.getLong(valuesRunner);

            int bitCount = Long.bitCount(xor);

            if (bitCount <= distance) {
                counter++;
            }

            valuesRunner += VALUE_SIZE;
        }

        return counter;
    }

    public synchronized int count(long value) {
        long valuesRunner = this.valuesPtr;
        for (int i = 0; i < size; i++) {
            if (value == UNSAFE.getLong(valuesRunner)) {
                return 1;
            }

            valuesRunner += VALUE_SIZE;
        }

        return 0;
    }

}
