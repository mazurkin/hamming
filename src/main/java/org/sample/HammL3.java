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
            int[] offsets = HammL3Mutators.INSTANCE.getOffsets(distance);
            int[][] counts = HammL3Mutators.INSTANCE.getCounts(distance);

            long v = value;
            int bcM0 = - Long.bitCount(v & 0xFF);
            int bcP0 = 16 + bcM0;
            v >>= 16;
            int bcM1 = - Long.bitCount(v & 0xFF);
            int bcP1 = 16 + bcM1;
            v >>= 16;
            int bcM2 = - Long.bitCount(v & 0xFF);
            int bcP2 = 16 + bcM2;
            v >>= 16;
            int bcM3 = - Long.bitCount(v & 0xFF);
            int bcP3 = 16 + bcM3;

            for (int i = 0; i < offsets.length; i++) {
                int[] sections = counts[i];

                if (sections[0] < bcM0 || bcP0 < sections[0]) {
                    continue;
                }
                if (sections[1] < bcM1 || bcP1 < sections[1]) {
                    continue;
                }
                if (sections[2] < bcM2 || bcP2 < sections[2]) {
                    continue;
                }
                if (sections[3] < bcM3 || bcP3 < sections[3]) {
                    continue;
                }

                int cellIndex = index + offsets[i];
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
            int[] offsets = HammL3Mutators.INSTANCE.getOffsets(distance);
            int[][] counts = HammL3Mutators.INSTANCE.getCounts(distance);

            long v = value;
            int bcM0 = - Long.bitCount(v & 0xFFFF);
            int bcP0 = 16 + bcM0;
            v >>= 16;
            int bcM1 = - Long.bitCount(v & 0xFFFF);
            int bcP1 = 16 + bcM1;
            v >>= 16;
            int bcM2 = - Long.bitCount(v & 0xFFFF);
            int bcP2 = 16 + bcM2;
            v >>= 16;
            int bcM3 = - Long.bitCount(v & 0xFFFF);
            int bcP3 = 16 + bcM3;

            for (int i = 0; i < offsets.length; i++) {
                int[] sections = counts[i];

                if (sections[0] < bcM0 || bcP0 < sections[0]) {
                    continue;
                }
                if (sections[1] < bcM1 || bcP1 < sections[1]) {
                    continue;
                }
                if (sections[2] < bcM2 || bcP2 < sections[2]) {
                    continue;
                }
                if (sections[3] < bcM3 || bcP3 < sections[3]) {
                    continue;
                }

                int cellIndex = index + offsets[i];
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
