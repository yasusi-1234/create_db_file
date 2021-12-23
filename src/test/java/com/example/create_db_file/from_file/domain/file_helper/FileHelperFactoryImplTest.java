package com.example.create_db_file.from_file.domain.file_helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileHelperFactoryImplのテスト")
class FileHelperFactoryImplTest {

    private FileHelperFactory fileHelperFactory;

    @BeforeEach
    void setUp() {
        FileInformationHelper excelHelper = new ExcelInformationHelper();
        FileInformationHelper csvHelper = new CsvInformationHelper();
        FileInformationHelper noneHelper = new NoneHelper();
        fileHelperFactory = new FileHelperFactoryImpl(excelHelper, csvHelper, noneHelper);
    }

    @Test
    @DisplayName("xlsx形式のファイル名が渡された場合はexcelHelperクラスを返却する")
    void createFileInformationHelperFromExcel() {
        String filename = "text.xlsx";
        FileInformationHelper actual = fileHelperFactory.createFileInformationHelper(filename);
        assertEquals(ExcelInformationHelper.class, actual.getClass());
    }

    @Test
    @DisplayName("csv形式のファイル名が渡された場合はcsvHelperクラスを返却する")
    void createFileInformationHelperFromCsv() {
        String filename = "text.csv";
        FileInformationHelper actual = fileHelperFactory.createFileInformationHelper(filename);
        assertEquals(CsvInformationHelper.class, actual.getClass());
    }
    @Test
    @DisplayName("xlsx・csv形式のファイル名以外が渡された場合はnoneHelperクラスを返却する")
    void createFileInformationHelperFromOther() {
        String filename = "text.txt";
        FileInformationHelper actual = fileHelperFactory.createFileInformationHelper(filename);
        assertEquals(NoneHelper.class, actual.getClass());
    }
}