package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.domain.file_helper.FileHelperFactory;
import com.example.create_db_file.from_file.domain.file_helper.FileInformationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assumptions.*;

@ExtendWith(MockitoExtension.class)
class DbFileCreateServiceImplTest {

    private static final String EXCEL_CONTENT_TYPE_NAME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CSV_CONTENT_TYPE_NAME = "text/csv";

    @Mock
    FileHelperFactory fileFactory;

    @Mock
    FileInformationHelper fileInformationHelper;

    DbFileCreateServiceImpl dbFileCreateService;

    @BeforeEach
    void setUp() {
        dbFileCreateService = new DbFileCreateServiceImpl(fileFactory);
        System.out.println("ok");
    }


    @DisplayName("findHeaderメソッド(MultipartFile)のテスト")
    @Nested
    class findHeader1 {
        @DisplayName("正常な処理")
        @Test
        void findHeader() throws Exception {
            MockMultipartFile file = testMultipartFile("excel/employee.xlsx", "test", EXCEL_CONTENT_TYPE_NAME);

            Map<Integer, String> map1 = new HashMap<>() {{
                put(1, "sample");
            }};

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn(map1).when(fileInformationHelper).analyzeHeader(any(InputStream.class));

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(file);

            assertEquals(map1, actualMap);
        }

        @DisplayName("異常系の処理")
        @Test
        void findHeaderOnIOException() throws Exception {
            MockMultipartFile file = Mockito.mock(MockMultipartFile.class);

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());

            doReturn("sample.txt").when(file).getOriginalFilename();
            doThrow(IOException.class).when(file).getInputStream();
            Map<Integer, String> actualMap = dbFileCreateService.findHeader(file);

            assertTrue(actualMap.isEmpty());
            verify(fileInformationHelper, never()).analyzeHeader(any(InputStream.class));
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

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn(expectedMap).when(fileInformationHelper).analyzeHeader(any(InputStream.class));

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);

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

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn(expectedMap).when(fileInformationHelper).analyzeHeader(any(InputStream.class));

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);

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

            Map<Integer, String> expectedMap = new HashMap<>();

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn(expectedMap).when(fileInformationHelper).analyzeHeader(any(InputStream.class));

            DBColumnsForm dbColumnsForm = new DBColumnsForm();

            Map<Integer, String> actualMap = dbFileCreateService.findHeader(resource.getFile().getAbsolutePath(), dbColumnsForm);
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

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn("").when(fileInformationHelper).saveFile(any(InputStream.class), anyString());
            // fileInformationHelperのsaveFileメソッドに渡す第二引数に渡すString値の確認用
            ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
            // 実行
            dbFileCreateService.fileToSaveTemporarily(multipartFile);

            verify(fileInformationHelper, times(1)).saveFile(any(InputStream.class), argumentCaptor.capture());

            // Pathの確認
            String actualFileName = Paths.get(argumentCaptor.getValue()).getFileName().toString();
            // フルpathが一致している
            String expectedFullPath = Paths.get(file.getAbsolutePath(), actualFileName).toString();
            assertEquals(expectedFullPath, argumentCaptor.getValue());
            // ファイル名が期待通りか
            assertAll(
                    () -> assertTrue(actualFileName.substring(0, 17).matches("[0-9]*_")),
                    () -> assertEquals(actualFileName.substring(17), multipartFile.getOriginalFilename())
            );
        }

        @Test
        @DisplayName("csvファイルの場合はcsvHelperのsaveFileが呼ばれ、Pathも正しく返却される")
        void fileToSaveTemporarilyCsv() throws Exception {
            File file = new File("/tmp");
            MockMultipartFile multipartFile = testMultipartFile("csv/employee.csv", "test", CSV_CONTENT_TYPE_NAME);

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn("").when(fileInformationHelper).saveFile(any(InputStream.class), anyString());
            // fileInformationHelperのsaveFileメソッドに渡す第二引数に渡すString値の確認用
            ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

            dbFileCreateService.fileToSaveTemporarily(multipartFile);

            verify(fileInformationHelper, times(1)).saveFile(any(InputStream.class), argumentCaptor.capture());

            System.out.println(argumentCaptor.getValue());

            // Pathの確認
            String actualFileName = Paths.get(argumentCaptor.getValue()).getFileName().toString();
            // フルpathが一致している
            String expectedFullPath = Paths.get(file.getAbsolutePath(), actualFileName).toString();
            assertEquals(expectedFullPath, argumentCaptor.getValue());
            // ファイル名が期待通りか
            assertAll(
                    () -> assertTrue(actualFileName.substring(0, 17).matches("[0-9]*_")),
                    () -> assertEquals(actualFileName.substring(17), multipartFile.getOriginalFilename())
            );
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

            doReturn(fileInformationHelper).when(fileFactory).createFileInformationHelper(anyString());
            doReturn(expectedSentence).when(fileInformationHelper).makeInsertSentence(any(InputStream.class), any(DBColumnsForm.class));

            String actual = dbFileCreateService.makeInsertSentence(resource.getFile().getAbsolutePath(), dbColumnsForm);
            // 戻り値が期待した値か
            assertEquals(expectedSentence, actual);
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