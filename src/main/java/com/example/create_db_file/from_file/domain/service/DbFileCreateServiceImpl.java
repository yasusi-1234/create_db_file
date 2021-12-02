package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.domain.file_helper.FileInformationHelper;
import com.example.create_db_file.from_file.domain.file_helper.ExcelInformationHelper;
import com.example.create_db_file.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DbFileCreateServiceImpl implements DbFileCreateService{

    private final FileInformationHelper excelHelper;

    private final FileInformationHelper csvHelper;

    @Autowired
    public DbFileCreateServiceImpl(
            @Qualifier("excelInformationHelper") FileInformationHelper excelHelper,
            @Qualifier("csvInformationHelper") FileInformationHelper csvHelper) {
        this.excelHelper = excelHelper;
        this.csvHelper = csvHelper;
    }

    /**
     * ExcelのHeader情報を、Key:位置情報 Value:ヘッダーの値を Mapに格納して返却するメソッド
     * @param multipartFile アップロードされたファイル情報
     * @return Key:位置情報 Value:ヘッダーの値を含んだMap 参考:{@link ExcelInformationHelper#analyzeHeader(InputStream)}
     */
    @Override
    public Map<Integer, String> findHeader(MultipartFile multipartFile){
        // ファイルの拡張子
        String extension = getFileExtension(multipartFile);

        try(InputStream in = multipartFile.getInputStream()){
            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                return excelHelper.analyzeHeader(in);
            }else if(Objects.equals(extension, "csv")){
                return csvHelper.analyzeHeader(in);
            }else {
                return new HashMap<>();
            }
        }catch (IOException e){
            log.warn("DbFileCreateServiceImpl: findHeader: IOException occur ");
            return new HashMap<>();
        }

    }

    /**
     * ExcelのHeader情報を、Key:位置情報 Value:ヘッダーの値を Mapに格納して返却するメソッド
     * @param filePath 保存されているファイルパス
     * @param form フォーム情報
     * @return Key:位置情報 Value:ヘッダーの値を含んだMap 参考:{@link ExcelInformationHelper#analyzeHeader(InputStream)}
     */
    @Override
    public Map<Integer, String> findHeader(String filePath, DBColumnsForm form){
        // ファイルの拡張子
        String extension = getFileExtension(filePath);

        Map<Integer, String> headerMap = new HashMap<>();
        try {
            Resource fileResource = new FileUrlResource(filePath);

            String simpleFileName = CommonUtils.getResourceSimpleFileName(fileResource);

            form.setTableName(simpleFileName);


            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                headerMap = excelHelper.analyzeHeader(fileResource.getInputStream());
                createDBColumnsForm(form, headerMap);
                return headerMap;
            }else if(Objects.equals(extension, "csv")){
                headerMap = csvHelper.analyzeHeader(fileResource.getInputStream());
                createDBColumnsForm(form, headerMap);
                return headerMap;
            }else {
                return headerMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return headerMap;
    }

    /**
     * Excelファイルを一時的なフォルダに保存する処理
     * 保存するデータのファイル名は時刻情報'yyyyMMddHHmm' と 第二引数のfileNameを
     * 連結したファイル名としている
     * @param multipartFile アップロードされたファイル情報
     * @return 保存した一時ファイルのPath
     */
    @Override
    public String fileToSaveTemporarily(MultipartFile multipartFile){
            File tempFile = new File("/tmp");
            // ファイルの拡張子
            String extension = getFileExtension(multipartFile);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
            String formatDate = LocalDateTime.now().format(formatter);
            String temporalFileName = formatDate + "_" + multipartFile.getOriginalFilename();

            Path path = Paths.get(tempFile.getAbsolutePath(), temporalFileName);
            // 一時ファイル保存
            try(InputStream in = multipartFile.getInputStream()){
                if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                    excelHelper.saveFile(in, path.toString());
                    return path.toString();
                }else if(Objects.equals(extension, "csv")){
                    csvHelper.saveFile(in, path.toString());
                    return path.toString();
                }else {
                    return "";
                }
            }catch (IOException ex){
                log.warn("DbFileCreateServiceImpl: fileToSaveTemporarily: IOException occur ");
                return "";
            }

    }

    /**
     * Excel用stream情報とform情報からinsert文を作成し、String型で返却するメソッド
     * 処理は事態は全て{@link ExcelInformationHelper}クラスに移譲している
     * @param filePath ファイルから得たファイルパス
     * @param form excelファイルから抽出したい情報などが纏められたFormクラス {@link DBColumnsForm}
     * @return Excelファイル、抽出したい情報から作成されたInset文の文字列 参考:{@link ExcelInformationHelper#makeInsertSentence(InputStream, DBColumnsForm)}
     */
    @Override
    public String makeInsertSentence(String filePath, DBColumnsForm form) throws IOException {
        Resource fileResource = new FileUrlResource(filePath);

        String extension = getFileExtension(filePath);

        if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
            return excelHelper.makeInsertSentence(fileResource.getInputStream(), form);
        }else if(Objects.equals(extension, "csv")){
            return csvHelper.makeInsertSentence(fileResource.getInputStream(), form);
        }else {
            return "";
        }
    }

    /**
     * マルチパートファイルの格納されているフィル名の拡張子を取得する
     * @param file multipartFile
     * @return file名の拡張子
     */
    private String getFileExtension(MultipartFile file){
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }

    /**
     * フィル名の拡張子を取得する
     * @param fileName ファイル名
     * @return file名の拡張子
     */
    private String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }

    /**
     * Excelファイルから読みだされたHeader情報(headerMap)より
     * DBColumnオブジェクトをそれぞれ生成し {@link DBColumn}
     * {@link DBColumnsForm} の　{@link DBColumnsForm#getColumns()} に値を格納する処理
     *
     * @param form {@link DBColumnsForm}
     * @param headerMap ヘッダー情報のマップ
     */
    private void createDBColumnsForm(DBColumnsForm form, Map<Integer, String> headerMap) {
        headerMap.entrySet().stream().map(entry -> DBColumn.of(entry.getValue(), entry.getKey())).forEach(form::addColumns);
    }
}
