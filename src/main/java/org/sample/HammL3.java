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
            HammL3Mutator[] mutators = HammL3Mutators.INSTANCE.getMutators(distance);

            long v = value;
            byte bcM0 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP0 = (byte) (16 + bcM0);
            v >>= 16;
            byte bcM1 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP1 = (byte) (16 + bcM1);
            v >>= 16;
            byte bcM2 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP2 = (byte) (16 + bcM2);
            v >>= 16;
            byte bcM3 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP3 = (byte) (16 + bcM3);

            for (HammL3Mutator mutator : mutators) {
                if (mutator.bc0 < bcM0 || bcP0 < mutator.bc0) {
                    continue;
                }
                if (mutator.bc1 < bcM1 || bcP1 < mutator.bc1) {
                    continue;
                }
                if (mutator.bc2 < bcM2 || bcP2 < mutator.bc2) {
                    continue;
                }
                if (mutator.bc3 < bcM3 || bcP3 < mutator.bc3) {
                    continue;
                }

                int cellIndex = index + mutator.offset;
                if (cells[cellIndex].contains(value, distance)) {
                    return true;
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
            HammL3Mutator[] mutators = HammL3Mutators.INSTANCE.getMutators(distance);

            long v = value;
            byte bcM0 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP0 = (byte) (16 + bcM0);
            v >>= 16;
            byte bcM1 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP1 = (byte) (16 + bcM1);
            v >>= 16;
            byte bcM2 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP2 = (byte) (16 + bcM2);
            v >>= 16;
            byte bcM3 = (byte) -Long.bitCount(v & 0x000000000000FFFF);
            byte bcP3 = (byte) (16 + bcM3);

            for (HammL3Mutator mutator : mutators) {
                if (mutator.bc0 < bcM0 || bcP0 < mutator.bc0) {
                    continue;
                }
                if (mutator.bc1 < bcM1 || bcP1 < mutator.bc1) {
                    continue;
                }
                if (mutator.bc2 < bcM2 || bcP2 < mutator.bc2) {
                    continue;
                }
                if (mutator.bc3 < bcM3 || bcP3 < mutator.bc3) {
                    continue;
                }

                int cellIndex = index + mutator.offset;
                counter += cells[cellIndex].count(value, distance);
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
