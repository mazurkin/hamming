package org.sample.generator;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MutationDumper {

    private static final int SPACE = 17 * 17 * 17 * 17;

    public static void main(String[] arguments) throws Exception {
        List<List<Integer>> buckets = new ArrayList<List<Integer>>(17);

        for (int i = 0; i <= 16; i++) {
            buckets.add(new ArrayList<Integer>(SPACE));
        }

        for (int bc1 = -16; bc1 <= 16; bc1++) {
            for (int bc2 = -16; bc2 <= 16; bc2++) {
                for (int bc3 = -16; bc3 <= 16; bc3++) {
                    for (int bc4 = -16; bc4 <= 16; bc4++) {
                        int bc = Math.abs(bc1) + Math.abs(bc2) + Math.abs(bc3) + Math.abs(bc4);
                        if (bc <= 16) {
                            int offset = bc4 + bc3 * 17 + bc2 * 17 * 17 + bc1 * 17 * 17 * 17;
                            if (-SPACE < offset && offset < +SPACE && offset != 0) {
                                for (int i = bc; i <= 16; i++) {
                                    buckets.get(i).add(offset);
                                }
                            }
                        }
                    }
                }
            }
        }

        OutputStream os = new FileOutputStream("mutators.dat");
        ObjectOutputStream obs = new ObjectOutputStream(os);

        for (int i = 0; i <= 16; i++) {
            Collection<Integer> offsets = buckets.get(i);

            System.out.printf("Bucket %d - length %d\n", i, offsets.size());

            obs.writeInt(offsets.size());
            for (int offset : offsets) {
                obs.writeInt(offset);
            }
        }

        obs.close();
        os.close();
    }
}
