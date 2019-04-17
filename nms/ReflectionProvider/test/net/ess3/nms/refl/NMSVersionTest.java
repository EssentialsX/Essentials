package net.ess3.nms.refl;

import net.ess3.nms.refl.ReflUtil.NMSVersion;

import org.junit.Assert;
import org.junit.Test;

public class NMSVersionTest {

    @Test
    public void testMajor() {
        NMSVersion v2_9_R1 = NMSVersion.fromString("v2_9_R1");

        Assert.assertEquals(2, v2_9_R1.getMajor());
        Assert.assertEquals(9, v2_9_R1.getMinor());
        Assert.assertEquals(1, v2_9_R1.getRelease());

        Assert.assertEquals(v2_9_R1.toString(), "v2_9_R1");

        Assert.assertTrue(v2_9_R1.isHigherThan(NMSVersion.fromString("v1_10_R1")));
        Assert.assertTrue(v2_9_R1.isHigherThanOrEqualTo(NMSVersion.fromString("v1_9_R1")));
    }

    @Test
    public void testMinor() {
        NMSVersion v1_10_R1 = NMSVersion.fromString("v1_10_R1");

        Assert.assertEquals(1, v1_10_R1.getMajor());
        Assert.assertEquals(10, v1_10_R1.getMinor());
        Assert.assertEquals(1, v1_10_R1.getRelease());

        Assert.assertEquals(v1_10_R1.toString(), "v1_10_R1");

        Assert.assertTrue(NMSVersion.fromString("v1_9_R1").isLowerThan(v1_10_R1));
        Assert.assertTrue(NMSVersion.fromString("v1_9_R1").isLowerThanOrEqualTo(v1_10_R1));
    }

    @Test
    public void testRelease() {
        NMSVersion v1_9_R2 = NMSVersion.fromString("v1_9_R2");

        Assert.assertEquals(1, v1_9_R2.getMajor());
        Assert.assertEquals(9, v1_9_R2.getMinor());
        Assert.assertEquals(2, v1_9_R2.getRelease());

        Assert.assertEquals(v1_9_R2.toString(), "v1_9_R2");
        Assert.assertEquals(v1_9_R2, NMSVersion.fromString("v1_9_R2"));

        Assert.assertTrue(v1_9_R2.isHigherThan(NMSVersion.fromString("v1_9_R1")));
    }
}
