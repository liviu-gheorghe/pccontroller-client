package com.liviugheorghe.pcc_client.util;

import androidx.core.util.Pair;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileInformationTest {

    public final double delta = 0.01;

    @Test(expected = NumberFormatException.class)
    public void withNullSize__getHumanReadableSizeShouldThrowNumberFormatException() {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setSize(null);
        fileInformation.getHumanReadableSize();
    }

    @Test(expected = NumberFormatException.class)
    public void withNonNumericString_getHumanReadableSizeShouldThrowNumberFormatException() {
        FileInformation fileInformation = new FileInformation();
        String nonNumericString = "Unknown size";
        fileInformation.setSize(nonNumericString);
        fileInformation.getHumanReadableSize();
    }

    @Test
    public void withBSize_getHumanReadableSizeShouldReturnTheSameSizeExpressedInB() {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setSize("437");
        Pair<Double, String> p = fileInformation.getHumanReadableSize();
        assertEquals(p.first, 437, delta);
        assertEquals(p.second, "B");
    }

    @Test
    public void withKBSize_getHumanReadableSizeShouldReturnTheCorrectSizeExpressedInKB() {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setSize("9000");
        Pair<Double, String> p = fileInformation.getHumanReadableSize();
        assertEquals(p.first, 9000 / 1024.0, delta);
        assertEquals(p.second, "KB");
    }

    @Test
    public void withMBSize_getHumanReadableSizeShouldReturnTheCorrectSizeExpressedInMB() {
        FileInformation fileInformation = new FileInformation();
        fileInformation.setSize("9000000");
        Pair<Double, String> p = fileInformation.getHumanReadableSize();
        assertEquals(p.first, 9000000 / (1024 * 1024.0), delta);
        assertEquals(p.second, "MB");
    }
}