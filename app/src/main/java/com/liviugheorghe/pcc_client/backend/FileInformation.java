package com.liviugheorghe.pcc_client.backend;

import java.io.InputStream;
import java.io.Serializable;

public class FileInformation implements Serializable {

    public String name;
    public String size;
    public InputStream inputStream;

    public FileInformation() {

    }
    /**
    FileInformation(String name, String size,InputStream inputStream) {
        this.name = name;
        this.size = size;
        this.inputStream = inputStream;
    }
     **/
}
