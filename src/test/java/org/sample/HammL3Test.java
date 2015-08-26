package org.sample;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class HammL3Test {

    private static final int CAPACITY = 10000;

    private Set<Long> values;

    private HammL3 hamm;

    @Before
    public void setUp() throws Exception {
        Random random = new Random(1);

        this.values = new HashSet<Long>(CAPACITY * 2);
        for (int i = 0; i < CAPACITY; i++) {
            this.values.add(random.nextLong());
        }

        this.hamm = new HammL3();
        for (long l : values) {
            this.hamm.add(l);
        }
    }

    @After
    public void tearDown() throws Exception {
        this.hamm.destroy();
    }

    @Test
    public void testContains() throws Exception {
        System.out.println("Checking distance = 0");
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 0));
        }

        System.out.println("Checking distance = 1");
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 1));
        }

        System.out.println("Checking distance = 2");
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 2));
        }

        System.out.println("Checking distance = 4");
        for (long l : values) {
            Assert.assertTrue(hamm.contains(l, 4));
        }

        System.out.println("Checking missing");
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