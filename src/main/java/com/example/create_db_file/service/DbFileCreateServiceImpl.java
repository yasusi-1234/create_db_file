package com.example.create_db_file.service;

import com.example.create_db_file.controller.form.DBColumnsForm;
import com.example.create_db_file.service.excel.ExcelInformationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
//        try {
//            File tempFile = resourceLoader.getResource("classpath:temporal").getFile();
            File tempFile = new File("/tmp");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
            String formatDate = LocalDateTime.now().format(formatter);
            String temporalFileName = formatDate + "_" + fileName;

            Path path = Paths.get(tempFile.getAbsolutePath(), temporalFileName);
            // 一時ファイル保存
            excelReader.saveExcelFile(in, path.toString());
            return path.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
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
}
