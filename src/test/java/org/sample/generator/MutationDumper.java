package org.sample.generator;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class MutationDumper {

    private static final int SPACE = 17 * 17 * 17 * 17;

    public static void main(String[] arguments) throws Exception {
        List<List<Integer>> offsetBuckets = new ArrayList<List<Integer>>(17);
        List<List<byte[]>> countBuckets = new ArrayList<List<byte[]>>(17);

        for (int i = 0; i <= 16; i++) {
            offsetBuckets.add(new ArrayList<Integer>(SPACE));
            countBuckets.add(new ArrayList<byte[]>(SPACE));
        }

        for (int bc3 = -16; bc3 <= 16; bc3++) {
            for (int bc2 = -16; bc2 <= 16; bc2++) {
                for (int bc1 = -16; bc1 <= 16; bc1++) {
                    for (int bc0 = -16; bc0 <= 16; bc0++) {
                        int bc = Math.abs(bc0) + Math.abs(bc1) + Math.abs(bc2) + Math.abs(bc3);
                        if (bc <= 16) {
                            int offset = bc0 + bc1 * 17 + bc2 * 17 * 17 + bc3 * 17 * 17 * 17;
                            if (-SPACE < offset && offset < +SPACE && offset != 0) {
                                for (int i = bc; i <= 16; i++) {
                                    offsetBuckets.get(i).add(offset);

                                    byte[] sections = { (byte) bc0, (byte) bc1, (byte) bc2, (byte) bc3 } ;
                                    countBuckets.get(i).add(sections);
                                }
                            }
                        }
                    }
                }
            }
        }

        OutputStream os = new FileOutputStream("mutators.dat.gz");
        GZIPOutputStream gzos = new GZIPOutputStream(os, 1048510);
        DataOutputStream dos = new DataOutputStream(gzos);

        for (int i = 0; i <= 16; i++) {
            List<Integer> offsets = offsetBuckets.get(i);
            List<byte[]> counts = countBuckets.get(i);

            System.out.printf("Distance %d - %d mutators\n", i, offsets.size());

            dos.writeInt(offsets.size());
            for (int j = 0; j < offsets.size(); j++) {
                byte[] sections = counts.get(j);
                dos.writeByte(sections[0]);
                dos.writeByte(sections[1]);
                dos.writeByte(sections[2]);
                dos.writeByte(sections[3]);

                int offset = offsets.get(j);
                dos.writeInt(offset);
            }
        }

        dos.close();
        gzos.close();
        os.close();
    }
}
