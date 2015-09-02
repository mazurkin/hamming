package org.sample;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class MutationDumper {

    private static final int SPACE = 17 * 17 * 17 * 17;

    public static void main(String[] arguments) throws Exception {
        List<List<HammL3Mutator>> distanceMutators = new ArrayList<List<HammL3Mutator>>(17);

        for (int i = 0; i <= 16; i++) {
            distanceMutators.add(new ArrayList<HammL3Mutator>(SPACE));
        }

        for (byte bc3 = -16; bc3 <= 16; bc3++) {
            for (byte bc2 = -16; bc2 <= 16; bc2++) {
                for (byte bc1 = -16; bc1 <= 16; bc1++) {
                    for (byte bc0 = -16; bc0 <= 16; bc0++) {
                        int bc = Math.abs(bc0) + Math.abs(bc1) + Math.abs(bc2) + Math.abs(bc3);
                        if (bc <= 16) {
                            int offset = bc0 + bc1 * 17 + bc2 * 17 * 17 + bc3 * 17 * 17 * 17;
                            if (-SPACE < offset && offset < +SPACE && offset != 0) {
                                HammL3Mutator mutator = new HammL3Mutator();
                                mutator.offset = offset;
                                mutator.bc0 = bc0;
                                mutator.bc1 = bc1;
                                mutator.bc2 = bc2;
                                mutator.bc3 = bc3;

                                for (int i = bc; i <= 16; i++) {
                                    distanceMutators.get(i).add(mutator);
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
            List<HammL3Mutator> mutators = distanceMutators.get(i);

            System.out.printf("Distance %d - %d mutators\n", i, mutators.size());

            dos.writeInt(mutators.size());
            for (HammL3Mutator mutator : mutators) {
                dos.writeByte(mutator.bc0);
                dos.writeByte(mutator.bc1);
                dos.writeByte(mutator.bc2);
                dos.writeByte(mutator.bc3);

                dos.writeInt(mutator.offset);
            }
        }

        dos.close();
        gzos.close();
        os.close();
    }
}
