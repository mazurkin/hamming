package org.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

final class HammL3Mutators {

    public static final HammL3Mutators INSTANCE = new HammL3Mutators();

    private int[][] mutators;

    private HammL3Mutators() {
        this.mutators = new int[17][];

        try {
            InputStream is = HammL3Mutators.class.getResourceAsStream("/mutators.dat");
            try {
                ObjectInputStream ois = new ObjectInputStream(is);
                try {
                    for (int i = 0; i <= 16; i++) {
                        int count = ois.readInt();

                        int[] offsets = new int[count];
                        for (int j = 0; j < count; j++) {
                            offsets[j] = ois.readInt();
                        }

                        mutators[i] = offsets;
                    }
                } finally {
                    ois.close();
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Fail to load mutator resource");
        }
    }

    public int[] getMutators(int distance) {
        return mutators[distance];
    }
}
