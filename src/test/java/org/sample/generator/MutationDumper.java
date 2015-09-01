package org.sample.generator;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MutationDumper {

    private static final int SPACE = 17 * 17 * 17 * 17;

    public static void main(String[] arguments) throws Exception {
        List<List<Integer>> offsetBuckets = new ArrayList<List<Integer>>(17);
        List<List<byte[]>> countBuckets = new ArrayList<List<byte[]>>(17);

        for (int i = 0; i <= 16; i++) {
            offsetBuckets.add(new ArrayList<Integer>(SPACE));
            countBuckets.add(new ArrayList<byte[]>(SPACE));
        }

        for (int bc4 = -16; bc4 <= 16; bc4++) {
            for (int bc3 = -16; bc3 <= 16; bc3++) {
                for (int bc2 = -16; bc2 <= 16; bc2++) {
                    for (int bc1 = -16; bc1 <= 16; bc1++) {
                        int bc = Math.abs(bc1) + Math.abs(bc2) + Math.abs(bc3) + Math.abs(bc4);
                        if (bc <= 16) {
                            int offset = bc1 + bc2 * 17 + bc3 * 17 * 17 + bc4 * 17 * 17 * 17;
                            if (-SPACE < offset && offset < +SPACE && offset != 0) {
                                for (int i = bc; i <= 16; i++) {
                                    offsetBuckets.get(i).add(offset);

                                    byte[] sections = { (byte) bc1, (byte) bc2, (byte) bc3, (byte) bc4 } ;
                                    countBuckets.get(i).add(sections);
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
            List<Integer> offsets = offsetBuckets.get(i);
            List<byte[]> counts = countBuckets.get(i);

            System.out.printf("Bucket %d - length %d\n", i, offsets.size());

            obs.writeInt(offsets.size());
            for (int j = 0; j < offsets.size(); j++) {
                byte[] sections = counts.get(j);
                obs.writeByte(sections[0]);
                obs.writeByte(sections[1]);
                obs.writeByte(sections[2]);
                obs.writeByte(sections[3]);

                int offset = offsets.get(j);
                obs.writeInt(offset);
            }
        }

        obs.close();
        os.close();
    }
}
