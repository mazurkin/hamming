package org.sample;

public class HammL3Cell {

    private static final double[] FACTORIALS = {
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

    private static final int CAPACITY_INIT_MIN = 1;

    private static final int CAPACITY_INIT_MAX = 8 * 1024;

    private static final double CAPACITY_DECIMATOR = 27435582641610000L / CAPACITY_INIT_MAX;

    private static final int CHECK_COUNTS_SIZE = 32;

    private final int bitCount;

    private final int bitCount1;

    private final int bitCount2;

    private final int bitCount3;

    private final int bitCount4;

    private long[] values;

    private long[] payloads;

    private int capacity;

    private int size;

    public HammL3Cell(int index) {
        this.size = 0;

        // Вычисляем секционные счетчики битов по индексу
        int i = index;
        this.bitCount1 = i % 17;
        i /= 17;
        this.bitCount2 = i % 17;
        i /= 17;
        this.bitCount3 = i % 17;
        i /= 17;
        this.bitCount4 = i % 17;

        // Суммарный счетчик битов
        this.bitCount = bitCount1 + bitCount2 + bitCount3 + bitCount4;

        // Число возможных вариантов long-числа теоретически попадающих под данный индекс рассчитывается как
        //
        // Q(index) = C(bitCount1,16) * C(bitCount2,16) * C(bitCount3,16) * C(bitCount4,16)
        // где С(k,n) = n! / k! / (n-k)!
        //
        // где максимальный вариант при bitCount1=bitCount2=bitCount3=bitCount4=8 будет равен
        // Q(8888@x17) = Q(41760@x10) = 12870 ^ 4 = 27435582641610000
        //
        // Поскольку принять вообще все теоретически возможные варианты у нас нет ни цели ни возможности нам
        // нужно просто выделить пропорциональный размер под массив. Если мы хотим хранить не более 16k элементов в
        // массиве вычисленный реальный размер нужно поделить на делитель:
        //      DECIMATOR = 27435582641610000 / 16384 = 1674535073340

        double realCapacity1 = FACTORIALS[16] / FACTORIALS[bitCount1] / FACTORIALS[16 - bitCount1];
        double realCapacity2 = FACTORIALS[16] / FACTORIALS[bitCount2] / FACTORIALS[16 - bitCount2];
        double realCapacity3 = FACTORIALS[16] / FACTORIALS[bitCount3] / FACTORIALS[16 - bitCount3];
        double realCapacity4 = FACTORIALS[16] / FACTORIALS[bitCount4] / FACTORIALS[16 - bitCount4];

        // Смасштабированная емкость ячейки
        double realCapacity = (realCapacity1 * realCapacity2 * realCapacity3 * realCapacity4) / CAPACITY_DECIMATOR;

        // Окончательно вычисляем емкость ячейки с учетом лимитов
        int capacity = (int) Math.round(realCapacity);
        capacity = Math.max(CAPACITY_INIT_MIN, capacity);
        capacity = Math.min(CAPACITY_INIT_MAX, capacity);
        this.capacity = capacity;

        // Выделяем массивы - под хэши и полезную нагрузку
        this.values = new long[capacity];
        this.payloads = new long[capacity];
    }

    public void destroy() {
        // do nothing
    }

    public synchronized void add(long value) {
        if (size == capacity) {
            int newCapacity = capacity * 2;

            long[] newValues = new long[newCapacity];
            long[] newPayloads = new long[newCapacity];

            System.arraycopy(values, 0, newValues, 0, size);
            System.arraycopy(payloads, 0, newPayloads, 0, size);

            this.values = newValues;
            this.payloads = newPayloads;
            this.capacity = newCapacity;
        }

        for (int i = 0; i < size; i++) {
            if (values[i] == value) {
                return;
            }
        }

        values[size] = value;
        payloads[size] = 0;

        size++;
    }

    public synchronized void remove(long value) {
        for (int i = 0; i < size; i++) {
            if (values[i] == value) {
                if (i < size - 1) {
                    values[i] = values[size - 1];
                    payloads[i] = payloads[size - 1];
                }

                size--;

                break;
            }
        }
    }

    public synchronized boolean contains(HammL3Context context) {
        if (!matches(context)) {
            return false;
        }

        long value = context.value;
        int distance = context.distance;

        for (int i = 0; i < size; i++) {
            long xor = value ^ values[i];

            int bitCount = Long.bitCount(xor);

            if (bitCount <= distance) {
                return true;
            }
        }

        return false;
    }

    public synchronized int count(HammL3Context context) {
        if (!matches(context)) {
            return 0;
        }

        long value = context.value;
        int distance = context.distance;
        int counter = 0;

        for (int i = 0; i < size; i++) {
            long xor = value ^ values[i];

            int bitCount = Long.bitCount(xor);

            if (bitCount <= distance) {
                counter++;
            }
        }

        return counter;
    }

    private boolean matches(HammL3Context context) {
        // Всегда выполняем легкую проверку на соответствие общего количества бит. Если общее количество битов
        // расходится то проверять этот массив нет смысла.
        if (this.bitCount < context.bitCountMin || this.bitCount > context.bitCountMax) {
            return false;
        }

        // Для достаточно больших массивов проверяем соответствие посекционных счетчиков битов. При слишком большом
        // суммарном расхождении посекционных счетчиков, которое превышает указанную в параметре метода дистанцию
        // проверять этот массив нет смысла.
        if (size > CHECK_COUNTS_SIZE) {
            int leastPossibleDistance = 0;
            if (context.bitCount1 > this.bitCount1) {
                leastPossibleDistance += context.bitCount1 - this.bitCount1;
            } else {
                leastPossibleDistance += this.bitCount1 - context.bitCount1;
            }
            if (context.bitCount2 > this.bitCount2) {
                leastPossibleDistance += context.bitCount2 - this.bitCount2;
            } else {
                leastPossibleDistance += this.bitCount2 - context.bitCount2;
            }
            if (context.bitCount3 > this.bitCount3) {
                leastPossibleDistance += context.bitCount3 - this.bitCount3;
            } else {
                leastPossibleDistance += this.bitCount3 - context.bitCount3;
            }
            if (context.bitCount4 > this.bitCount4) {
                leastPossibleDistance += context.bitCount4 - this.bitCount4;
            } else {
                leastPossibleDistance += this.bitCount4 - context.bitCount4;
            }

            if (leastPossibleDistance > context.distance) {
                return false;
            }
        }

        return true;
    }

}
