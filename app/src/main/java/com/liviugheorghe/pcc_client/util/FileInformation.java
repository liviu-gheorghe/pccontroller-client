package com.liviugheorghe.pcc_client.util;

import android.net.Uri;

import androidx.core.util.Pair;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileInformation {

    private Uri uri;
    private String name;
    private String size;
    private String type = "";


    public FileInformation(Uri uri, String name, String size, String type) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    public FileInformation() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        try {
            Pair<Double, String> p = getHumanReadableSize();
            return p.first + " " + p.second;
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Pair<Double, String> getHumanReadableSize() throws NumberFormatException {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        double numberOfBytes;
        try {
            numberOfBytes = Double.parseDouble(this.size);
        } catch (NumberFormatException | NullPointerException e) {
            throw new NumberFormatException();
        }

        if (numberOfBytes < (1 << 10)) {
            return new Pair<>(numberOfBytes, "B");
        }
        if (numberOfBytes < (1 << 20)) {
            Double formattedValue = Double.valueOf(decimalFormat.format(numberOfBytes / (1 << 10)));
            return new Pair<>(formattedValue, "KB");
        }
        Double formattedValue = Double.valueOf(decimalFormat.format(numberOfBytes / (1 << 20)));
        return new Pair<>(formattedValue, "MB");
    }
}
