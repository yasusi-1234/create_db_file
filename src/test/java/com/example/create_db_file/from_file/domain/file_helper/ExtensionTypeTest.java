package com.example.create_db_file.from_file.domain.file_helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assumptions.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExtensionTypeクラスのテスト")
class ExtensionTypeTest {

    @DisplayName("getExtensionTypeメソッドはファイル名を受け取ると、それに対応したExtensionTypeを返却するる")
    @ParameterizedTest
    @MethodSource("fileNames")
    void getExtensionType(String data) {
        int index = data.lastIndexOf(".");
        String extension = data.substring(index + 1);

        assumingThat(extension.equals("xlsx") || extension.equals("xls"),
                () -> assertEquals(ExtensionType.Excel, ExtensionType.getExtensionType(data)));
        assumingThat(extension.equals("csv"),
                () -> assertEquals(ExtensionType.Csv, ExtensionType.getExtensionType(data)));
        assumingThat(!extension.equals("xlsx") && !extension.equals("xls") && !extension.equals("csv"),
                () -> assertEquals(ExtensionType.Other, ExtensionType.getExtensionType(data)));
    }

    @DisplayName("getExtensionTypeFromExtensionメソッドは拡張子文字列を受け取ると、それに対応したExtensionTypeを返却するる")
    @ParameterizedTest
    @MethodSource("extensions")
    void getExtensionTypeFromExtension(String data) {
        assumingThat(data.equals("xlsx") || data.equals("xls"),
                () -> assertEquals(ExtensionType.Excel, ExtensionType.getExtensionTypeFromExtension(data)));
        assumingThat(data.equals("csv"),
                () -> assertEquals(ExtensionType.Csv, ExtensionType.getExtensionTypeFromExtension(data)));
        assumingThat(!data.equals("xlsx") && !data.equals("xls") && !data.equals("csv"),
                () -> assertEquals(ExtensionType.Other, ExtensionType.getExtensionTypeFromExtension(data)));
    }

    static Stream<String> fileNames(){
        return Stream.of("sample.txt", "data.csv", "data.xlsx", "data.xls", "data.json");
    }

    static Stream<String> extensions(){
        return Stream.of("txt", "csv", "xlsx", "xls", "json");
    }

}