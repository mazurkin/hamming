package org.sample;

public class HammL3 {

    private static final int SPACE = 17 * 17 * 17 * 17;

    private final HammL3Cell[] cells;

    private final HammL3Stat stat;

    public HammL3() {
        this.stat = new HammL3Stat();
        this.cells = new HammL3Cell[SPACE];

        for (int i = 0; i < SPACE; i++) {
            this.cells[i] = new HammL3Cell(i, stat);
        }
    }

    public void destroy() {
        for (HammL3Cell cell : cells) {
            cell.destroy();
        }
    }

    public void add(long value) {
        int index = HammL3Util.getIndex(value);

        cells[index].add(value);
    }

    public void remove(long value) {
        int index = HammL3Util.getIndex(value);

        cells[index].remove(value);
    }

    public boolean contains(long value, int distance) {
        if (distance < 0 || distance > 16) {
            throw new IllegalArgumentException("Distance is illegal");
        }

        int index = HammL3Util.getIndex(value);

        if (cells[index].contains(value)) {
            return true;
        }

        if (distance > 0) {
            int bitCount = Long.bitCount(value);
            int bitCountMin = Math.max(bitCount - distance, 0);
            int bitCountMax = Math.min(bitCount + distance, 64);

            int[] offsets = HammL3Mutators.INSTANCE.getMutators(distance);

            for (int offset : offsets) {
                int cellIndex = index + offset;
                if (0 <= cellIndex && cellIndex < SPACE) {
                    HammL3Cell cell = cells[cellIndex];
                    int cellBitCount = cell.getBitCount();
                    if (bitCountMin <= cellBitCount && cellBitCount <= bitCountMax) {
                        if (cell.contains(value, distance)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public int count(long value, int distance) {
        if (distance < 0 || distance > 16) {
            throw new IllegalArgumentException("Distance is illegal");
        }

        int counter = 0;

        int index = HammL3Util.getIndex(value);
        counter += cells[index].count(value);

        if (distance > 0) {
            int bitCount = Long.bitCount(value);
            int bitCountMin = Math.max(bitCount - distance, 0);
            int bitCountMax = Math.min(bitCount + distance, 64);

            int[] offsets = HammL3Mutators.INSTANCE.getMutators(distance);

            for (int offset : offsets) {
                int cellIndex = index + offset;
                if (0 <= cellIndex && cellIndex < SPACE) {
                    HammL3Cell cell = cells[cellIndex];
                    int cellBitCount = cell.getBitCount();
                    if (bitCountMin <= cellBitCount && cellBitCount <= bitCountMax) {
                        counter += cell.count(value, distance);
                    }
                }
            }
        }

        return counter;
    }

    public int getStatCapacity() {
        return this.stat.capacity.get();
    }

    public int getStatReallocs() {
        return this.stat.reallocs.get();
    }

    public int getStatMoves() {
        return this.stat.moves.get();
    }

}
