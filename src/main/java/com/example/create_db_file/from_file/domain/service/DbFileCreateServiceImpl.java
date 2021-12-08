package com.example.create_db_file.from_file.domain.service;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.domain.file_helper.ExtensionType;
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

@Slf4j
@Service
public class DbFileCreateServiceImpl implements DbFileCreateService {

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
     *
     * @param multipartFile アップロードされたファイル情報
     * @return Key:位置情報 Value:ヘッダーの値を含んだMap 参考:{@link ExcelInformationHelper#analyzeHeader(InputStream)}
     */
    @Override
    public Map<Integer, String> findHeader(MultipartFile multipartFile) {
        // ファイルの拡張子
        ExtensionType extensionType = ExtensionType.getExtensionType(multipartFile.getOriginalFilename());

        try (InputStream in = multipartFile.getInputStream()) {
            switch (extensionType) {
                case Excel:
                    return excelHelper.analyzeHeader(in);
                case Csv:
                    return csvHelper.analyzeHeader(in);
                case Other:
                default:
                    return new HashMap<>();
            }
        } catch (IOException e) {
            log.warn("DbFileCreateServiceImpl: findHeader: IOException occur ");
            return new HashMap<>();
        }

    }

    /**
     * ExcelのHeader情報を、Key:位置情報 Value:ヘッダーの値を Mapに格納して返却するメソッド
     * また正しくMapに情報が得られた場合はフォーム情報にそれに適合した値をセットする
     *
     * @param filePath 保存されているファイルパス
     * @param form     フォーム情報
     * @return Key:位置情報 Value:ヘッダーの値を含んだMap 参考:{@link ExcelInformationHelper#analyzeHeader(InputStream)}
     */
    @Override
    public Map<Integer, String> findHeader(String filePath, DBColumnsForm form) {

        Map<Integer, String> headerMap = new HashMap<>();
        try {
            Resource fileResource = new FileUrlResource(filePath);
            // ファイルの拡張子
            ExtensionType extensionType = ExtensionType.getExtensionType(filePath);

            switch (extensionType) {
                case Excel:
                    headerMap = excelHelper.analyzeHeader(fileResource.getInputStream());
                    createDBColumnsForm(form, headerMap, fileResource);
                    return headerMap;
                case Csv:
                    headerMap = csvHelper.analyzeHeader(fileResource.getInputStream());
                    createDBColumnsForm(form, headerMap, fileResource);
                    return headerMap;
                case Other:
                default:
                    return headerMap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return headerMap;
    }

    /**
     * Excelファイルを一時的なフォルダに保存する処理
     * 保存するデータのファイル名は時刻情報'yyyyMMddHHmmssSS' と 第二引数のfileNameを
     * 連結したファイル名としている
     *
     * @param multipartFile アップロードされたファイル情報
     * @return 保存した一時ファイルのPath
     */
    @Override
    public String fileToSaveTemporarily(MultipartFile multipartFile) {
        // 一時ファイル保存
        try (InputStream in = multipartFile.getInputStream()) {

            File tempFile = new File("/tmp");
            // ファイルの拡張子
            ExtensionType extensionType = ExtensionType.getExtensionType(multipartFile.getOriginalFilename());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
            String formatDate = LocalDateTime.now().format(formatter);
            // 一時保存ファイルのファイル名設定
            String temporalFileName = formatDate + "_" + multipartFile.getOriginalFilename();
            // 一時保存ファイルの保存するパス設定
            Path path = Paths.get(tempFile.getAbsolutePath(), temporalFileName);

            switch (extensionType) {
                case Excel:
                    excelHelper.saveFile(in, path.toString());
                    return path.toString();
                case Csv:
                    csvHelper.saveFile(in, path.toString());
                    return path.toString();
                case Other:
                default:
                    return "";
            }

        } catch (IOException ex) {
            log.warn("DbFileCreateServiceImpl: fileToSaveTemporarily: IOException occur ");
            return "";
        }

    }

    /**
     * Excel用stream情報とform情報からinsert文を作成し、String型で返却するメソッド
     * 処理は事態は全て{@link ExcelInformationHelper}クラスに移譲している
     *
     * @param filePath ファイルから得たファイルパス
     * @param form     excelファイルから抽出したい情報などが纏められたFormクラス {@link DBColumnsForm}
     * @return Excelファイル、抽出したい情報から作成されたInset文の文字列 参考:{@link ExcelInformationHelper#makeInsertSentence(InputStream, DBColumnsForm)}
     */
    @Override
    public String makeInsertSentence(String filePath, DBColumnsForm form) throws IOException {
        Resource fileResource = new FileUrlResource(filePath);
        // ファイルの拡張子
        ExtensionType extensionType = ExtensionType.getExtensionType(filePath);

        switch (extensionType) {
            case Excel:
                return excelHelper.makeInsertSentence(fileResource.getInputStream(), form);
            case Csv:
                return csvHelper.makeInsertSentence(fileResource.getInputStream(), form);
            case Other:
            default:
                return "";
        }
    }

    /**
     * Excelファイルから読みだされたHeader情報(headerMap)とファイルリソース情報より {@link DBColumnsForm}に
     * テーブル名をセットし、またDBColumnオブジェクトをそれぞれ生成し {@link DBColumn}
     * {@link DBColumnsForm} の　{@link DBColumnsForm#getColumns()} に値を格納する処理
     *
     * @param form      {@link DBColumnsForm}
     * @param headerMap ヘッダー情報のマップ
     */
    private void createDBColumnsForm(DBColumnsForm form, Map<Integer, String> headerMap, Resource fileResource) {
        String simpleFileName = CommonUtils.getResourceSimpleFileName(fileResource);
        // フォームのテーブル名をセット
        form.setTableName(simpleFileName);
        // フォームのカラムオブジェクトをセット
        headerMap.entrySet().stream().map(entry -> DBColumn.of(entry.getValue(), entry.getKey())).forEach(form::addColumns);
    }
}
