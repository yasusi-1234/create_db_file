package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.domain.file_helper.CsvInformationHelper;
import com.example.create_db_file.from_file.domain.file_helper.ExcelInformationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DbFileCreateServiceImpl implements DbFileCreateService{

    private final ExcelInformationReader excelReader;

    private final CsvInformationHelper csvHelper;

    private final ResourceLoader resourceLoader;

    /**
     * ExcelのHeader情報を、Key:位置情報 Value:ヘッダーの値を Mapに格納して返却するメソッド
     * @param in excelファイルから得たInputStream
     * @return Key:位置情報 Value:ヘッダーの値を含んだMap 参考:{@link ExcelInformationReader#analyzeHeader(InputStream)}
     */
    @Override
    public Map<Integer, String> findHeader(InputStream in){
        return excelReader.analyzeHeader(in);
    }

    @Override
    public Map<Integer, String> findCsvHeader(InputStream in){
        return csvHelper.analyzeCsvHeader(in);
    }

    /**
     * Excelファイルを一時的なフォルダに保存する処理
     * 保存するデータのファイル名は時刻情報'yyyyMMddHHmm' と 第二引数のfileNameを
     * 連結したファイル名としている
     * @param in excelファイルから得たInputStream
     * @param fileName excelのファイル名
     * @return 保存した一時ファイルのPath
     */
    @Override
    public String fileToSaveTemporarily(InputStream in, String fileName){
            File tempFile = new File("/tmp");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
            String formatDate = LocalDateTime.now().format(formatter);
            String temporalFileName = formatDate + "_" + fileName;

            Path path = Paths.get(tempFile.getAbsolutePath(), temporalFileName);
            // 一時ファイル保存
            excelReader.saveExcelFile(in, path.toString());
            return path.toString();
    }

    @Override
    public String csvFileToSaveTemporarily(InputStream in, String fileName){
        File tempFile = new File("/tmp");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
        String formatDate = LocalDateTime.now().format(formatter);
        String temporalFileName = formatDate + "_" + fileName;

        Path path = Paths.get(tempFile.getAbsolutePath(), temporalFileName);

        csvHelper.saveCsvFile(in, path.toString());
        return path.toString();
    }

    /**
     * Excel用stream情報とform情報からinsert文を作成し、String型で返却するメソッド
     * 処理は事態は全て{@link ExcelInformationReader}クラスに移譲している
     * @param in excelファイルから得たInputStream
     * @param form excelファイルから抽出したい情報などが纏められたFormクラス {@link DBColumnsForm}
     * @return Excelファイル、抽出したい情報から作成されたInset文の文字列 参考:{@link ExcelInformationReader#makeInsertSentence(InputStream, DBColumnsForm)}
     */
    @Override
    public String callMakeInsertSentence(InputStream in, DBColumnsForm form){
        return excelReader.makeInsertSentence(in, form);
    }

    /**
     * Csv用stream情報とform情報からinsert文を作成し、String型で返却するメソッド
     * 処理は事態は全てクラスに移譲している
     * @param in csvファイルから得たInputStream
     * @param form csvファイルから抽出したい情報などが纏められたFormクラス
     * @return Csvファイル、抽出したい情報から作成されたInset文の文字列 参考:
     */
    @Override
    public String callCsvMakeInsertSentence(InputStream in, DBColumnsForm form){
        return csvHelper.makeInsertSentence(in, form);
    }
}
