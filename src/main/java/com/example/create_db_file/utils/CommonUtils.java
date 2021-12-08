package com.example.create_db_file.utils;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Paths;

public class CommonUtils {

    public static String getResourceSimpleFileName(Resource resource){

        try {

            String fileName = Paths.get(resource.getFile().getPath()).getFileName().toString();
            int firstIndex = fileName.indexOf("_");
            int lastIndex = fileName.indexOf(".");

            return fileName.substring(firstIndex + 1, lastIndex);
        } catch (IOException e) {
            return "sample";
        }
    }
}
