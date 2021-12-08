package com.example.create_db_file.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommonUtilsクラスのテスト")
class CommonUtilsTest {

    @Test
    @DisplayName("getResourceSimpleFileNameメソッドは拡張子抜きのファイル名の文字列を正しく返却する")
    void getResourceSimpleFileName(@TempDir Path tempDir) throws Exception {
        // セットアップ
        Path numbers = tempDir.resolve("numbers.txt");
        Resource resource = new FileUrlResource(numbers.toString());
        String actual = CommonUtils.getResourceSimpleFileName(resource);

        String expected = "numbers";

        assertEquals(expected, actual);
    }
}