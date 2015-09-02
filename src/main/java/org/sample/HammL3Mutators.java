package org.sample;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

final class HammL3Mutators {

    public static final HammL3Mutators INSTANCE = new HammL3Mutators();

    private static final String RESOURCE = "/mutators.dat.gz";

    private final HammL3Mutator[][] distanceMutators;

    private HammL3Mutators() {
        this.distanceMutators = new HammL3Mutator[17][];

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

                        HammL3Mutator[] mutators = new HammL3Mutator[count];

                        for (int j = 0; j < count; j++) {
                            HammL3Mutator mutator = new HammL3Mutator();

                            mutator.bc0 = dis.readByte();
                            mutator.bc1 = dis.readByte();
                            mutator.bc2 = dis.readByte();
                            mutator.bc3 = dis.readByte();
                            mutator.offset = dis.readInt();

                            mutators[j] = mutator;
                        }

                        this.distanceMutators[i] = mutators;
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

    public HammL3Mutator[] getMutators(int distance) {
        return this.distanceMutators[distance];
    }
}
