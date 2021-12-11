package com.example.create_db_file.from_file.controller.form;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class OriginalDataFileFormTest {

    @Autowired
    Validator validator;

    OriginalDataFileForm form;
    BindingResult bindingResult;

    private static final String EXCEL_RESOURCE_PATH = "excel/employee.xlsx";
    private static final String CSV_RESOURCE_PATH = "csv/employee.csv";
    private static final String TEXT_RESOURCE_PATH = "other/other.txt";
    private static final String JSON_RESOURCE_PATH = "other/other.json";

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String TEXT_CONTENT_TYPE = "text/plain";

    @DisplayName("ExpectedFileがファイル形式csv, xlsx形式のファイルは正しく通過させる")
    @ParameterizedTest
    @CsvSource({
            EXCEL_RESOURCE_PATH + ", sample, " + EXCEL_CONTENT_TYPE,
            CSV_RESOURCE_PATH + ", sample, " + CSV_CONTENT_TYPE
    })
    void multipartFileSuccess(String resourcePath, String name, String contentType) throws Exception {
        // setUP
        MultipartFile multipartFile = testMultipartFile(resourcePath, name, contentType);
        form = new OriginalDataFileForm();
        form.setMultipartFile(multipartFile);
        bindingResult = new BindException(form, "form");
        // 実行
        validator.validate(form, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }

    @DisplayName("ExpectedFileがファイル形式csv, xlsx形式以外の場合はValidatorにより引っかかる")
    @ParameterizedTest
    @CsvSource({
            TEXT_RESOURCE_PATH + ", sample, " + TEXT_CONTENT_TYPE,
            JSON_RESOURCE_PATH + ", sample, " + MediaType.APPLICATION_JSON_VALUE
    })
    void multipartFileFail(String resourcePath, String name, String contentType) throws Exception {
        // setUP
        MultipartFile multipartFile = testMultipartFile(resourcePath, name, contentType);
        form = new OriginalDataFileForm();
        form.setMultipartFile(multipartFile);
        bindingResult = new BindException(form, "form");
        // 実行
        validator.validate(form, bindingResult);

        assertTrue(bindingResult.hasErrors());

        assertEquals("※ サポートされていないファイル形式です。", bindingResult.getFieldError("multipartFile").getDefaultMessage());
    }

    @DisplayName("ExpectedFileがFileがNullの場合はチェックを通過させない")
    @Test
    void multipartFileNull(){
        form = new OriginalDataFileForm();
        bindingResult = new BindException(form, "form");
        // 実行
        validator.validate(form, bindingResult);

        assertTrue(bindingResult.hasErrors());
        // メッセージの確認
        assertEquals(bindingResult.getFieldError("multipartFile").getDefaultMessage(), "※ ファイルが選択されていません。");
    }

    private static MultipartFile testMultipartFile(String resourcePath, String name, String contentType) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        return new MockMultipartFile(name, resource.getFile().getName(), contentType, resource.getInputStream());
    }

}