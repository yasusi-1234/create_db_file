package com.example.create_db_file.from_file.domain.file_helper;

import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoneHelperクラスのテスト")
@ExtendWith(MockitoExtension.class)
class NoneHelperTest {

    private FileInformationHelper noneHelper;

    private InputStream in;

    @BeforeEach
    void setUp() {
        noneHelper = new NoneHelper();
        in = Mockito.mock(InputStream.class);
    }

    @Test
    @DisplayName("analyzeHeaderは無条件で空のHashMapを返却する")
    void analyzeHeader() {
        Map<Integer, String> actualMap = noneHelper.analyzeHeader(in);
        assertTrue(actualMap.isEmpty());
    }

    @Test
    @DisplayName("saveFileは無条件で空文字を返却する")
    void saveFile() {
        String testPath = "test.txt";
        String actual = noneHelper.saveFile(in, testPath);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("makeInsertSentenceは無条件で空文字を返却する")
    void makeInsertSentence() {
        String actual = noneHelper.makeInsertSentence(in, new DBColumnsForm());
        assertTrue(actual.isEmpty());
    }

}