package com.example.create_db_file.from_file.domain.file_helper;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { CsvInformationHelper.class })
@DisplayName("CsvInformationHelperクラスのテスト")
class CsvInformationHelperTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    @Qualifier("csvInformationHelper")
    private FileInformationHelper csvInformationHelper;

    @DisplayName("analyzeHeaderメソッドはCsvファイルのInputStream情報からヘッダーの値を抜き出し、Mapオブジェクトとして返却する")
    @Test
    void analyzeHeader() throws Exception{
        Resource resource = resourceLoader.getResource("classpath:csv/employee.csv");
        Map<Integer, String> actual = csvInformationHelper.analyzeHeader(resource.getInputStream());

        Map<Integer, String> expected = new HashMap<>(){{
            put(0, "id");
            put(1, "first_name");
            put(2, "last_name");
            put(3, "age");
            put(4, "department");
        }};

        assertEquals(actual, expected);

        Resource inEmptyResource = resourceLoader.getResource("classpath:csv/in-empty-employee2.csv");
        Map<Integer, String> inEmptyActual = csvInformationHelper.analyzeHeader(inEmptyResource.getInputStream());

        assertEquals(inEmptyActual, expected);
    }

    @DisplayName("analyzeHeaderメソッドはファイルがヘッダーのみか何もデータが無い場合は空のMapを返却する")
    @Test
    void analyzeHeaderFromNoDataOrHeaderDataOnly() throws Exception{
        // ヘッダーもデータも空のCsvリソース
        Resource resourceNoData = resourceLoader.getResource("classpath:csv/allnodata.csv");
        Map<Integer, String> noDataActual = csvInformationHelper.analyzeHeader(resourceNoData.getInputStream());
        assertTrue(noDataActual.isEmpty());

        // ヘッダー情報のみ記載されたリソース
        Resource resourceHeaderOnly = resourceLoader.getResource("classpath:csv/nodata.csv");

        Map<Integer, String> headerOnlyActual = csvInformationHelper.analyzeHeader(resourceHeaderOnly.getInputStream());
        assertTrue(headerOnlyActual.isEmpty());
    }

    @DisplayName("saveFileメソッドはcsvのInputStream情報を指定したPathに保存する")
    @Test
    void saveFile(@TempDir Path tempDir) throws Exception{
        Resource resource = resourceLoader.getResource("classpath:csv/employee.csv");

        Path savePath = Paths.get(tempDir.toString(), "sample.csv");
        csvInformationHelper.saveFile(resource.getInputStream(), savePath.toString());

        assertTrue(Files.exists(savePath));
    }

    @Nested
    @DisplayName("makeInsertSentence関連のテスト")
    class makeInsertSentence{

        @Test
        @DisplayName("makeInsertSentenceメソッドは期待された量のInsert文を生成する")
        void makeInsertSentenceOfCount() throws Exception{
            DBColumnsForm form = testDbColumnsForm();
            Resource resource = resourceLoader.getResource("classpath:csv/employee.csv");

            String actual = csvInformationHelper.makeInsertSentence(resource.getInputStream(), form);
            int actualCount = sentenceDataList(actual).size();

            assertEquals(10, actualCount);
        }

        @Test
        @DisplayName("makeInsertSentenceメソッドは期待されたInsert文の右側の文字列を正しく生成する")
        void makeInsertSentenceOfOneLine1() throws Exception{
            DBColumnsForm form = testDbColumnsForm();
            Resource resource = resourceLoader.getResource("classpath:csv/employee.csv");

            String actual = csvInformationHelper.makeInsertSentence(resource.getInputStream(), form);
            List<String> sentences = cutRightDataList(actual);

            String sentenceOfFirstLine = sentences.get(0);

            String expected = "'1', '太郎', '山田', '23', '総務部');";
            assertEquals(expected, sentenceOfFirstLine);
        }

        @Test
        @DisplayName("makeInsertSentenceメソッドは条件に応じて期待されたInsert文の右側の文字列を正しく生成する")
        void makeInsertSentenceOfOneLine2() throws Exception{
            DBColumnsForm form = testDbColumnsForm();
            // id に数値を設定
            form.getColumns().get(0).setType(DBColumn.ColumnType.NUMBER);
            // first_name を除外
            form.getColumns().get(1).setInclude(false);
            // ageをNULLに変換
            form.getColumns().get(3).setType(DBColumn.ColumnType.NULL);

            Resource resource = resourceLoader.getResource("classpath:csv/employee.csv");

            String actual = csvInformationHelper.makeInsertSentence(resource.getInputStream(), form);
            List<String> sentences = cutRightDataList(actual);

            String sentenceOfFirstLine = sentences.get(0);

            String expected = "1, '山田', NULL, '総務部');";
            assertEquals(expected, sentenceOfFirstLine);
        }

        @Test
        @DisplayName("makeInsertTemplateLeftメソッドはフォーム情報を元にInsert文の左側の文字列を正しく生成する")
        void makeInsertTemplateLeft1() throws Exception {
            Method method = csvInformationHelper.getClass()
                    .getDeclaredMethod("makeInsertTemplateLeft", DBColumnsForm.class);
            method.setAccessible(true);

            String actual = (String) method.invoke(csvInformationHelper, testDbColumnsForm());

            String expected = "INSERT INTO sample (id, first_name, last_name, age, department) VALUES(";
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("makeInsertTemplateLeftメソッドはフォーム情報に代替のカラム名が含まれる場合正しく入れ替えて出力する")
        void makeInsertTemplateLeft2() throws Exception {
            Method method = csvInformationHelper.getClass()
                    .getDeclaredMethod("makeInsertTemplateLeft", DBColumnsForm.class);
            method.setAccessible(true);

            DBColumnsForm form = testDbColumnsForm();
            form.getColumns().get(0).setChangeColumnName("employee_id");
            form.getColumns().get(4).setChangeColumnName("department_name");

            String actual = (String) method.invoke(csvInformationHelper, form);

            String expected = "INSERT INTO sample (employee_id, first_name, last_name, age, department_name) VALUES(";
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("makeInsertTemplateLeftメソッドはフォーム情報に除外が含まれる場合正しく除外して出力する")
        void makeInsertTemplateLeft3() throws Exception {
            Method method = csvInformationHelper.getClass()
                    .getDeclaredMethod("makeInsertTemplateLeft", DBColumnsForm.class);
            method.setAccessible(true);

            DBColumnsForm form = testDbColumnsForm();
            form.getColumns().get(0).setInclude(false);
            form.getColumns().get(3).setInclude(false);

            String actual = (String) method.invoke(csvInformationHelper, form);

            String expected = "INSERT INTO sample (first_name, last_name, department) VALUES(";
            assertEquals(expected, actual);
        }

        /**
         * サンプル用 {@link DBColumnsForm}
         *
         */
        DBColumnsForm testDbColumnsForm(){
            DBColumnsForm form = new DBColumnsForm();
            form.setFileName("test");
            form.setTableName("sample");
            form.setColumns(testDbColumns());

            return form;
        }

        /**
         * サンプル用 {@link DBColumn} を5件作成し返却
         *
         */
        List<DBColumn> testDbColumns(){
            DBColumn dbColumn1 = DBColumn.of("id", 0);
            DBColumn dbColumn2 = DBColumn.of("first_name", 1);
            DBColumn dbColumn3 = DBColumn.of("last_name", 2);
            DBColumn dbColumn4 = DBColumn.of("age", 3);
            DBColumn dbColumn5 = DBColumn.of("department", 4);
            return new ArrayList<>(Arrays.asList(dbColumn1, dbColumn2, dbColumn3, dbColumn4, dbColumn5));
        }

        List<String> cutRightDataList(String sentence){
            return sentenceDataList(sentence).stream().filter(StringUtils::hasText)
                    .map(this::cutRightData).collect(Collectors.toList());
        }

        /**
         * Insert文の一行間隔をリスト化して返却
         * @param sentence Insert文の文字列
         */
        List<String> sentenceDataList(String sentence){
            Pattern p = Pattern.compile("[I].*[;]");
            Matcher matcher = p.matcher(sentence);
            List<String> sentences = new ArrayList<>();
            while(matcher.find()){
                sentences.add(matcher.group());
            }
            return sentences;
        }

        /**
         * Insert分のValues(以降から;までを切り出して返却する
         * @param sentence Insert文文字列
         */
        String cutRightData(String sentence){
            int start = sentence.lastIndexOf("(");
            int end = sentence.lastIndexOf(")");
            return sentence.substring(start + 1 , end + 2);
        }

    }
}