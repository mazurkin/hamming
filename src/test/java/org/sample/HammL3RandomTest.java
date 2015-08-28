package org.sample;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class HammL3RandomTest {

    private static final int CAPACITY = 10000;

    private static Set<Long> values;

    private static HammL3 hamm;

    @BeforeClass
    public static void setUp() throws Exception {
        Random random = new Random(1);

        values = new HashSet<Long>(CAPACITY * 2);
        for (int i = 0; i < CAPACITY; i++) {
            values.add(random.nextLong());
        }

        hamm = new HammL3();
        for (long l : values) {
            hamm.add(l);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hamm.destroy();
    }

    @Test
    public void testContainsDistance0() throws Exception {
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 0));
        }
    }

    @Test
    public void testContainsDistance1() throws Exception {
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 1));
        }
    }

    @Test
    public void testContainsDistance2() throws Exception {
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 2));
        }
    }

    @Test
    public void testMissing() {
        Random random = new Random(1);
        for (int i = 0; i < CAPACITY; ) {
            long l = random.nextLong();
            if (values.contains(l)) {
                continue;
            }

            Assert.assertFalse(hamm.contains(l, 0));

            i++;
        }
    }

    @Test
    public void testCount() throws Exception {
        int distance = 20;

        Iterator<Long> iterator = values.iterator();
        long value = iterator.next();

        int expected = 0;
        for (long l : values) {
            long xor = l ^ value;
            if (Long.bitCount(xor) <= distance) {
                expected++;
            }
        }

        int found = hamm.count(value, distance);

        Assert.assertEquals(expected, found);

        System.out.printf("Found %d with distance %d in %d items\n", found, distance, values.size());
    }
}