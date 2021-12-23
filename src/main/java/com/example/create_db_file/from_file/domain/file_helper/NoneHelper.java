package com.example.create_db_file.from_file.domain.file_helper;

import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component("noneHelper")
public class NoneHelper implements FileInformationHelper{
    @Override
    public Map<Integer, String> analyzeHeader(InputStream in) {
        return new HashMap<>();
    }

    @Override
    public String saveFile(InputStream in, String newFilePath) {
        return "";
    }

    @Override
    public String makeInsertSentence(InputStream in, DBColumnsForm form) {
        return "";
    }
}
