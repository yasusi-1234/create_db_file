package com.example.create_db_file.domain.service.csv;

import com.example.create_db_file.controller.form.DBColumnsForm;

import java.io.InputStream;
import java.util.Map;

public interface CsvInformationHelper {
    Map<Integer, String> analyzeCsvHeader(InputStream in);

    void saveCsvFile(InputStream in, String newFilePath);

    String makeInsertSentence(InputStream in, DBColumnsForm form);
}
