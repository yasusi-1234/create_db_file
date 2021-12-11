package com.example.create_db_file.from_file.controller.form;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DBColumnsFormのValidationテスト")
@SpringBootTest(classes = { ValidationAutoConfiguration.class })
class DBColumnsFormTest {

    @Autowired
    Validator validator;

    private DBColumnsForm form;
    private BindingResult bindingResult;

    @Nested
    @DisplayName("filedのfileNameのテスト")
    class fieldNameTest {

        @DisplayName("問題無いファイル名のテスト")
        @ParameterizedTest
        @ValueSource(strings = {"sample", "test", "サンプル", "テスト", "本日の帳票"})
        void fieldNameSuccess(String fileName){
            // setUp
            form = new DBColumnsForm();
            form.setFileName(fileName);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            // fieldNameが問題無いかのテスト
            assertFalse(bindingResult.hasFieldErrors("fileName"));
        }

        @DisplayName("問題のあるファイル名のテスト")
        @ParameterizedTest
        @ValueSource(strings = {"sample.txt", "te@st", "サン~プル", "テス\\ト"})
        void fieldNameFail(String fileName){
            // setUp
            form = new DBColumnsForm();
            form.setFileName(fileName);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            // fieldNameが問題無いかのテスト
            assertTrue(bindingResult.hasFieldErrors("fileName"));
            // エラーメッセージの確認
            assertEquals(bindingResult.getFieldError("fileName").getDefaultMessage(), "※使用できない文字が含まれています");
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("filedのtableNameのテスト")
    class tableNameTest {

        @DisplayName("Nullとスペースのみの場合検証に引っかかる")
        @ParameterizedTest
        @MethodSource("tableNameSource")
        void tableNameFailed(String tableName) {
            // setUp
            form = new DBColumnsForm();
            form.setTableName(tableName);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            assertTrue(bindingResult.hasFieldErrors("tableName"));
            // メッセージの確認
            assertEquals("※テーブル名を入力してください", bindingResult.getFieldError("tableName").getDefaultMessage());
        }

        @DisplayName("文字列が1文字以上指定されている場合検証に引っかからない")
        @ParameterizedTest
        @ValueSource(strings = {"a", "sample", "Sample"})
        void tableNameSuccess(String tableName) {
            // setUp
            form = new DBColumnsForm();
            form.setTableName(tableName);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            assertFalse(bindingResult.hasFieldErrors("tableName"));
        }

        Stream<String> tableNameSource(){
            return Stream.of("  ", "    ", null);
        }
    }

    @Nested
    @DisplayName("fieldのcolumnsのテスト")
    class columnsTest {

        @DisplayName("columnsが空の場合は検査に引っかかる")
        @Test
        void columnsOfEmpty() {
            // setUp
            form = new DBColumnsForm();
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            // 検査
            assertTrue(bindingResult.hasFieldErrors("columns"));
        }

        @DisplayName("columnsのcolumnのincludeが全てfalseの場合は検査に引っかかる")
        @Test
        void columnsOfAllFalseInclude() {
            // setUp
            form = new DBColumnsForm();
            setColumnsAllIncludeNull(form);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            // 検査
            assertTrue(bindingResult.hasFieldErrors("columns"));
        }

        @DisplayName("columnsのcolumnが1つ以上設定されている場合は検査を通過する")
        @Test
        void columnsSuccess() {
            // setUp
            form = new DBColumnsForm();
            setColumns(form);
            bindingResult = new BindException(form, "form");
            // 実行
            validator.validate(form, bindingResult);
            // 検査
            assertFalse(bindingResult.hasFieldErrors("columns"));
        }

        void setColumns(DBColumnsForm form){
            form.addColumns(DBColumn.of("sample1", 0));
            form.addColumns(DBColumn.of("sample2", 1));
            form.addColumns(DBColumn.of("sample3", 2));
        }

        void setColumnsAllIncludeNull(DBColumnsForm form){
            form.addColumns(DBColumn.of("sample1", 0));
            form.getColumns().get(0).setInclude(false);
            form.addColumns(DBColumn.of("sample2", 1));
            form.getColumns().get(1).setInclude(false);
            form.addColumns(DBColumn.of("sample3", 2));
            form.getColumns().get(2).setInclude(false);
        }
    }
}