package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.domain.file_helper.CsvInformationHelper;
import com.example.create_db_file.from_file.domain.file_helper.ExcelInformationHelper;
import com.example.create_db_file.from_file.domain.file_helper.FileInformationHelper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assumptions.*;


@SpringBootTest(classes = {ExcelInformationHelper.class, CsvInformationHelper.class})
class DbFileCreateServiceImplTest {

    private static final String EXCEL_CONTENT_TYPE_NAME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CSV_CONTENT_TYPE_NAME = "text/csv";

    @Mock(name = "excelHelper")
    FileInformationHelper excelHelper;
    @Mock(name = "csvHelper")
    FileInformationHelper csvHelper;

    //    @InjectMocks
    DbFileCreateServiceImpl dbFileCreateService;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
        dbFileCreateService = new DbFileCreateServiceImpl(excelHelper, csvHelper);
        System.out.println("ok");
    }

    @Nested
    @DisplayName("findHeaderメソッド(MultipartFile)関連のテスト")
    class findHeaderTest1 {

        @DisplayName("拡張子がxlsxまたはxlsのファイルが渡された場合、excelHelperのanalyzeHeaderメソッドを呼び出す")
        @Test
        void findHeaderExcel() throws Exception {
            MockMultipartFile file = testMultipartFile("excel/employee.xlsx", "test", EXCEL_CONTENT_TYPE_NAME);

            Map<Integer, String> map1 = new HashMap<>() {{
                put(1, "sample");
            }};
            Map<Integer, String> map2 = new HashMap<>() {{
                put(2, "test");
            }};

            doReturn(map1).when(excelHelper).analyzeHeader(any(InputStream.class));
            doReturn(map2).when(csvHelper).analyzeHeader(any(InputStream.class));

            Map<Integer, String> actual = dbFileCreateService.findHeader(file);
            // excelHelperのanalyzeHeaderが一回呼び出されているかテスト
            verify(excelHelper, times(1)).analyzeHeader(any(InputStream.class));
            // csvHelperのanalyzeHeaderは呼び出されていない事の確認
            verify(csvHelper, never()).analyzeHeader(any(InputStream.class));

            assertEquals(map1, actual);
        }

        @Test
        @DisplayName("拡張子がcsvのファイルが渡された場合、csvHelperのanalyzeHeaderメソッドを呼び出す")
        void findHeaderCsv() throws Exception {
            MockMultipartFile file = testMultipartFile("csv/employee.csv", "test", CSV_CONTENT_TYPE_NAME);

            Map<Integer, String> map1 = new HashMap<>() {{
                put(1, "sample");
            }};
            Map<Integer, String> map2 = new HashMap<>() {{
                put(2, "test");
            }};

            doReturn(map1).when(excelHelper).analyzeHeader(any(InputStream.class));
            doReturn(map2).when(csvHelper).analyzeHeader(any(InputStream.class));

            Map<Integer, String> actual = dbFileCreateService.findHeader(file);
            // csvHelperのanalyzeHeaderが一回呼び出されているかテスト
            verify(csvHelper, times(1)).analyzeHeader(any(InputStream.class));
            // csvHelperのanalyzeHeaderは一呼び出されていない事の確認
            verify(excelHelper, never()).analyzeHeader(any(InputStream.class));

            assertEquals(map2, actual);
        }

        @Test
        @DisplayName("拡張子がcsv,xlsx,xls以外の物は空のMapを返却する")
        void findHeaderOther() throws Exception {
            MockMultipartFile file = testMultipartFile("other/other.txt", "test", "text/plain");

            Map<Integer, String> map1 = new HashMap<>() {{
                put(1, "sample");
            }};
            Map<Integer, String> map2 = new HashMap<>() {{
                put(2, "test");
            }};

            doReturn(map1).when(excelHelper).analyzeHeader(any(InputStream.class));
            doReturn(map2).when(csvHelper).analyzeHeader(any(InputStream.class));

            Map<Integer, String> actual = dbFileCreateService.findHeader(file);
            // excelHelperのanalyzeHeaderは呼び出されていない事の確認
            verify(excelHelper, never()).analyzeHeader(any(InputStream.class));
            // csvHelperのanalyzeHeaderは呼び出されていない事の確認
            verify(csvHelper, never()).analyzeHeader(any(InputStream.class));
            // 空のMapが返却されている事のテスト
            assertTrue(actual.isEmpty());
        }
    }

    @Nested
    @DisplayName("findHeaderメソッド(String, DbColumnsForm)関連のテスト")
    class findHeaderTest2 {
        @Test
        @DisplayName("xlsxファイルを渡すとヘッダー情報のマップを返却しDbColumnsFormの設定も行う")
        void findHeaderExcel() throws Exception {
            Resource resource = new ClassPathResource("excel/employee.xlsx");
            // id, first_name, last_name, age, department のマップ
            Map<Integer, String> expectedMap = testMap();
            doReturn(expectedMap).when(excelHelper).analyzeHeader(any(InputStream.class));

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // excelHelperのanalyzeHeaderが一回呼ばれている事のテスト
            verify(excelHelper, times(1)).analyzeHeader(any(InputStream.class));
            // csvHelperのanalyzeHeaderは呼ばれていない事の確認
            verify(csvHelper, never()).analyzeHeader(any(InputStream.class));
            // 期待した値が返却される事のテスト
            assertEquals(expectedMap, actualMap);

            // フォームの設定されている値のテスト
            // 設定されたDbColumnが五つである
            assertEquals(5, dbColumnsForm.getColumns().size());
            // 中身の確認
            List<DBColumn> dbColumns = dbColumnsForm.getColumns();
            assertAll(
                    () -> assertEquals("employee", dbColumnsForm.getTableName()),
                    () -> assertEquals(0, dbColumns.get(0).getColumnIndex()),
                    () -> assertEquals("id", dbColumns.get(0).getColumnName()),
                    () -> assertEquals(4, dbColumns.get(4).getColumnIndex()),
                    () -> assertEquals("department", dbColumns.get(4).getColumnName())
            );
        }

        @Test
        @DisplayName("csvファイルを渡すとヘッダー情報のマップを返却しDbColumnsFormの設定も行う")
        void findHeaderCsv() throws Exception {
            Resource resource = new ClassPathResource("csv/employee.csv");
            // id, first_name, last_name, age, department のマップ
            Map<Integer, String> expectedMap = testMap();
            doReturn(expectedMap).when(csvHelper).analyzeHeader(any(InputStream.class));

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // csvHelperのanalyzeHeaderが一回呼ばれている事のテスト
            verify(csvHelper, times(1)).analyzeHeader(any(InputStream.class));
            // excelHelperのanalyzeHeaderは呼ばれていない事の確認
            verify(excelHelper, never()).analyzeHeader(any(InputStream.class));
            // 期待した値が返却される事のテスト
            assertEquals(expectedMap, actualMap);

            // フォームの設定されている値のテスト
            // 設定されたDbColumnが五つである
            assertEquals(5, dbColumnsForm.getColumns().size());
            // 中身の確認
            List<DBColumn> dbColumns = dbColumnsForm.getColumns();
            assertAll(
                    () -> assertEquals("employee", dbColumnsForm.getTableName()),
                    () -> assertEquals(0, dbColumns.get(0).getColumnIndex()),
                    () -> assertEquals("id", dbColumns.get(0).getColumnName()),
                    () -> assertEquals(4, dbColumns.get(4).getColumnIndex()),
                    () -> assertEquals("department", dbColumns.get(4).getColumnName())
            );
        }

        @Test
        @DisplayName("excel,csv以外のファイルを渡すと空のMap返却しDbColumnsFormの設定は行わない")
        void findHeaderOther() throws Exception {
            Resource resource = new ClassPathResource("other/other.txt");

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // csvHelperのanalyzeHeaderは呼ばれていない事のテスト
            verify(csvHelper, never()).analyzeHeader(any(InputStream.class));
            // excelHelperのanalyzeHeaderは呼ばれていない事のテスト
            verify(excelHelper, never()).analyzeHeader(any(InputStream.class));
            // 期待した値が返却される事のテスト
            assertTrue(actualMap.isEmpty());

            // フォームの設定されている値のテスト
            // DbColumnのリストは空
            assertTrue(dbColumnsForm.getColumns().isEmpty());
            // テーブル名も設定されていない
            assertNull(dbColumnsForm.getTableName());
        }
    }

    @Nested
    @DisplayName("fileToSaveTemporarilyのテスト")
    class fileToSaveTemporarily {
        @Test
        @DisplayName("xlsxファイルの場合はexcelHelperのsaveFileが呼ばれ、Pathも正しく返却される")
        void fileToSaveTemporarilyExcel() throws Exception {
            File file = new File("/tmp");
            MockMultipartFile multipartFile = testMultipartFile("excel/employee.xlsx", "test", EXCEL_CONTENT_TYPE_NAME);

            String actualPath = dbFileCreateService.fileToSaveTemporarily(multipartFile);
            // excelHelperのsaveFileメソッドが1回呼び出されたか確認
            verify(excelHelper, times(1)).saveFile(any(InputStream.class), anyString());
            // cavHelperの方のメソッドは呼ばれていない事の確認
            verify(csvHelper, never()).saveFile(any(InputStream.class), anyString());

            // Pathの確認
            String actualFileName = Paths.get(actualPath).getFileName().toString();
            // フルpathが一致している
            String expectedFullPath = Paths.get(file.getAbsolutePath(), actualFileName).toString();
            assertEquals(expectedFullPath, actualPath);
            // ファイル名が期待通りか
            assertAll(
                    () -> assertTrue(actualFileName.substring(0, 17).matches("[0-9]*_")),
                    () -> assertEquals(actualFileName.substring(17), multipartFile.getOriginalFilename())
            );
        }

        @Test
        @DisplayName("csvファイルの場合はcsvHelperのsaveFileが呼ばれ、Pathも正しく返却される")
        void fileToSaveTemporarilyCsv() throws Exception {
            MockMultipartFile multipartFile = testMultipartFile("csv/employee.csv", "test", CSV_CONTENT_TYPE_NAME);

            String actualPath = dbFileCreateService.fileToSaveTemporarily(multipartFile);
            // csvHelperのsaveFileメソッドが1回呼び出されたか確認
            verify(csvHelper, times(1)).saveFile(any(InputStream.class), anyString());
            // excelHelperの方のメソッドは呼ばれていない事の確認
            verify(excelHelper, never()).saveFile(any(InputStream.class), anyString());
        }

        @Test
        @DisplayName("excel,csvファイル以外の場合はsaveFileメソッドは呼ばれない、Pathは空の値が返却される")
        void fileToSaveTemporarilyOther() throws Exception {
            MockMultipartFile multipartFile = testMultipartFile("other/other.txt", "test", "text/plain");

            String actualPath = dbFileCreateService.fileToSaveTemporarily(multipartFile);
            // excelHelper,csvHelperのsaveFileメソッドは呼ばれていない事の確認
            verify(csvHelper, never()).saveFile(any(InputStream.class), anyString());
            verify(excelHelper, never()).saveFile(any(InputStream.class), anyString());
            // 空の文字列がへんきゃくされる
            assertTrue(actualPath.isEmpty());
        }
    }

    @Nested
    @DisplayName("makeInsertSentenceメソッド関連のテスト")
    class makeInsertSentenceTest {

        DBColumnsForm dbColumnsForm;

        @BeforeEach
        void setUp(){
            dbColumnsForm = new DBColumnsForm();
        }

        @DisplayName("ファイル形式がExcelの場合のテスト")
        @Test
        void makeInsertSentenceExcel() throws Exception {
            Resource resource = new ClassPathResource("excel/employee.xlsx");
            String expectedSentence = "this is excel file";

            doReturn(expectedSentence).when(excelHelper).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));

            String actual = dbFileCreateService.makeInsertSentence(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // 戻り値が期待した値か
            assertEquals(expectedSentence, actual);
            // excelHelperのmakeInsertSentenceメソッドが1回呼ばれている事の確認
            verify(excelHelper, times(1)).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
            // csvHelperのmakeInsertSentenceメソッドは呼ばれていない事の確認
            verify(csvHelper, never()).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
        }

        @DisplayName("ファイル形式がCsvの場合のテスト")
        @Test
        void makeInsertSentenceCsv() throws Exception {
            Resource resource = new ClassPathResource("csv/employee.csv");
            String expectedSentence = "this is csv file";

            doReturn(expectedSentence).when(csvHelper).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));

            String actual = dbFileCreateService.makeInsertSentence(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // 戻り値が期待した値か
            assertEquals(expectedSentence, actual);
            // csvHelperのmakeInsertSentenceメソッドが1回呼ばれている事の確認
            verify(csvHelper, times(1)).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
            // excelHelperのmakeInsertSentenceメソッドは呼ばれていない事の確認
            verify(excelHelper, never()).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
        }

        @DisplayName("ファイル形式がcsv,xlsx以外の場合のテスト")
        @Test
        void makeInsertSentenceOther() throws Exception {
            Resource resource = new ClassPathResource("other/other.txt");

            String actual = dbFileCreateService.makeInsertSentence(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // 戻り値が期待した値か
            assertTrue(actual.isEmpty());
            // csvHelperとexcelHelperのmakeInsertSentenceメソッドが呼ばれていない事の確認
            verify(csvHelper, never()).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
            verify(excelHelper, never()).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));
        }

    }

    private static MockMultipartFile testMultipartFile(String sourcePath, String name, String contentType) throws IOException {
        Resource resource = new ClassPathResource(sourcePath);
        return new MockMultipartFile(name, resource.getFile().getName(), contentType, resource.getInputStream());
    }

    private static Map<Integer, String> testMap() {
        return new HashMap<>() {{
            put(0, "id");
            put(1, "first_name");
            put(2, "last_name");
            put(3, "age");
            put(4, "department");
        }};
    }
}