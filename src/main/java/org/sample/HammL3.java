package org.sample;

public class HammL3 {

    private static final int SPACE = /* 17^4 */ 83521;

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
        int index = getIndex(value);

        cells[index].add(value);
    }

    public void remove(long value) {
        int index = getIndex(value);

        cells[index].remove(value);
    }

    public boolean contains(long value, int distance) {
        HammL3Context context = new HammL3Context(value, distance);

        for (int i = context.indexMin; i <= context.indexMax; i++) {
            if (cells[i].contains(context)) {
                return true;
            }
        }

        return false;
    }

    public int count(long value, int distance) {
        HammL3Context context = new HammL3Context(value, distance);

        int counter = 0;

        for (int i = context.indexMin; i <= context.indexMax; i++) {
            counter += cells[i].count(context);
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

    private static int getIndex(long value) {
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

}
