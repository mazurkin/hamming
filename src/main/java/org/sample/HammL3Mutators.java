package org.sample;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

final class HammL3Mutators {

    public static final HammL3Mutators INSTANCE = new HammL3Mutators();

    private static final String RESOURCE = "/mutators.dat.gz";

    private final int[][] offsets;
    private final int[][][] counts;

    private HammL3Mutators() {
        this.offsets = new int[17][];
        this.counts = new int[17][][];

        try {
            loadResource();
        } catch (IOException e) {
            throw new IllegalStateException("Fail to load mutator resource file", e);
        }
    }

    private void loadResource() throws IOException {
        InputStream is = HammL3Mutators.class.getResourceAsStream(RESOURCE);
        try {
            GZIPInputStream gzis = new GZIPInputStream(is);
            try {
                DataInputStream dis = new DataInputStream(gzis);
                try {
                    for (int i = 0; i <= 16; i++) {
                        int count = dis.readInt();

                        int[] offsets = new int[count];
                        int[][] counts = new int[count][];

                        for (int j = 0; j < count; j++) {
                            int[] sections = new int[4];
                            sections[0] = dis.readByte();
                            sections[1] = dis.readByte();
                            sections[2] = dis.readByte();
                            sections[3] = dis.readByte();
                            counts[j] = sections;

                            offsets[j] = dis.readInt();
                        }

                        this.offsets[i] = offsets;
                        this.counts[i] = counts;
                    }
                } finally {
                    dis.close();
                }
            } finally {
                gzis.close();
            }
        } finally {
            is.close();
        }
    }

    public int[] getOffsets(int distance) {
        return this.offsets[distance];
    }

    public int[][] getCounts(int distance) {
        return this.counts[distance];
    }
}
