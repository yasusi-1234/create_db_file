package com.example.create_db_file.domain.service;

import com.example.create_db_file.controller.form.DBColumnsForm;

import java.io.InputStream;
import java.util.Map;

public interface DbFileCreateService {
    Map<Integer, String> findHeader(InputStream in);

    Map<Integer, String> findCsvHeader(InputStream in);

    String fileToSaveTemporarily(InputStream in, String fileName);

    String csvFileToSaveTemporarily(InputStream in, String fileName);

    String callMakeInsertSentence(InputStream in, DBColumnsForm form);

    String callCsvMakeInsertSentence(InputStream in, DBColumnsForm form);
}
