package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DbFileCreateService {
    Map<Integer, String> findHeader(MultipartFile multipartFile);

    Map<Integer, String> findHeader(String filePath, DBColumnsForm form);

    String fileToSaveTemporarily(MultipartFile multipartFile);

    String makeInsertSentence(String filePath, DBColumnsForm form) throws IOException;
}
