package org.sample;

import java.util.Random;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class HammL3SimpleTest {

    private static final int CAPACITY = 10000;

    private static Set<Long> values;

    private static HammL3 hamm;

    @BeforeClass
    public static void setUp() throws Exception {
        Random random = new Random(1);

        hamm = new HammL3();
        hamm.add(0x1000100010001333L);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hamm.destroy();
    }

    @Test
    public void test() throws Exception {
        Assert.assertTrue(hamm.contains(0x1000100010001333L, 0));
        Assert.assertFalse(hamm.contains(0x1000100010001332L, 0));

        Assert.assertTrue(hamm.contains(0x1000100010001333L, 1));
        Assert.assertTrue(hamm.contains(0x1000100010001332L, 1));
        Assert.assertTrue(hamm.contains(0x1000100010000333L, 1));
        Assert.assertTrue(hamm.contains(0x1000100000001333L, 1));
        Assert.assertTrue(hamm.contains(0x0000100010001333L, 1));
        Assert.assertFalse(hamm.contains(0x1010101010001333L, 1));

        Assert.assertTrue(hamm.contains(0x1000100010001333L, 2));
        Assert.assertTrue(hamm.contains(0x1000100010001322L, 2));
        Assert.assertTrue(hamm.contains(0x1000000000001333L, 2));
        Assert.assertTrue(hamm.contains(0x0000100010001332L, 2));
        Assert.assertFalse(hamm.contains(0x1010101010101333L, 2));
    }

}