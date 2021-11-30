package com.example.create_db_file.from_file.controller;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.controller.form.OriginalDataFileForm;
import com.example.create_db_file.session.UserSession;
import com.example.create_db_file.from_file.domain.service.DbFileCreateService;
import com.example.create_db_file.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@SessionAttributes(types = {UserSession.class})
@Slf4j
public class DbFileCreateController {

    @ModelAttribute("userSession")
    public UserSession userSession() {
        return new UserSession();
    }

    private final DbFileCreateService dbFileCreateService;

    @GetMapping("/home")
    public String getHome(
            @RequestParam(name = "useInfo", defaultValue = "false") boolean useInfo,
            @ModelAttribute("originalDataFileForm") OriginalDataFileForm form,
            Model model) {
        model.addAttribute("useInfo", useInfo);
        return "home";
    }

    @GetMapping("/getSample")
    public String getSampleData(
            @RequestParam(required = false, name = "filename")String filename,
            @RequestParam(required = false, name = "extension")String extension,
            @ModelAttribute("originalDataFileForm") OriginalDataFileForm form,
            Model model) {
        if(StringUtils.hasText(filename) && StringUtils.hasText(extension)){
            model.addAttribute("filename", filename);
            model.addAttribute("extension", extension);
            return "dataFileView";
        }

        return "home";
    }

    @PostMapping("/upload")
    public String postUpload(
            @Validated @ModelAttribute("originalDataFileForm") OriginalDataFileForm form,
            BindingResult bindingResult,
            Model model,
            @ModelAttribute("userSession") UserSession userSession,
            @ModelAttribute("dBColumnsForm") DBColumnsForm dbColumnsForm,
            UriComponentsBuilder uriComponentsBuilder) {
        if (bindingResult.hasErrors()) {
            return "home";
        }
        // ファイルの拡張子
        String extension = getFileExtension(form.getMultipartFile());

        try (InputStream in = form.getMultipartFile().getInputStream()) {
            Map<Integer, String> resultMap = new HashMap<>();

            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                resultMap = dbFileCreateService.findHeader(in);
            }else if(Objects.equals(extension, "csv")){
                resultMap = dbFileCreateService.findCsvHeader(in);
            }

            if (resultMap.isEmpty()) {
                // ヘッダーの値が設定されていない、またはデータ部分が無い場合(処理できない為)
                model.addAttribute("noHeader",
                        "※ヘッダーの値が読み取れないか、あるいはデータが情報が不足しています。ファイルを確認してください。");
                return "home";
            }

            // ヘッダーの値・データの値が正常だった場合の処理
            // ファイルを一時保存する処理

            String temporalFileName = null;

            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                temporalFileName = dbFileCreateService
                        .fileToSaveTemporarily(form.getMultipartFile().getInputStream(),
                                form.getMultipartFile().getOriginalFilename());
            }else if(Objects.equals(extension, "csv")){
                temporalFileName = dbFileCreateService
                        .csvFileToSaveTemporarily(form.getMultipartFile().getInputStream(),
                                form.getMultipartFile().getOriginalFilename());
            }

            if (Objects.nonNull(userSession.getTemporalFilePath())
                    && Files.exists(Path.of(userSession.getTemporalFilePath()))) {
                // セッションに既にファイルがpathが格納されている、かつファイルも存在する
                Files.delete(Path.of(userSession.getTemporalFilePath()));
            }

            userSession.setTemporalFilePath(temporalFileName);

            String redirectPath = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder)
                    .withMappingName("DFCC#getUpload").encode().build();

            return "redirect:" + redirectPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "home";
    }

    @GetMapping(path = "/upload")
    public String getUpload(
            @Validated @ModelAttribute("originalDataFileForm") OriginalDataFileForm form,
            BindingResult bindingResult,
            Model model,
            @ModelAttribute("userSession") UserSession userSession,
            @ModelAttribute("dBColumnsForm") DBColumnsForm dbColumnsForm) {
        if (!StringUtils.hasText(userSession.getTemporalFilePath())) {
            return "home";
        }

        try {
            Resource fileResource = new FileUrlResource(userSession.getTemporalFilePath());

            String simpleFileName = CommonUtils.getResourceSimpleFileName(fileResource);

            dbColumnsForm.setTableName(simpleFileName);

            String extension = getFileExtension(fileResource.getFilename());

            Map<Integer, String> resultMap = new HashMap<>();
            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                resultMap = dbFileCreateService.findHeader(fileResource.getInputStream());
            }else if(Objects.equals(extension, "csv")){
                resultMap = dbFileCreateService.findCsvHeader(fileResource.getInputStream());
            }

            model.addAttribute("columnType", DBColumn.ColumnType.values());
            createDBColumnsForm(dbColumnsForm, resultMap);
            return "custom";
        } catch (IOException e) {
            e.printStackTrace();
            return "home";
        }


    }

    @PostMapping(path = "create", params = "create")
    public String createRequest(
            @Validated @ModelAttribute("dBColumnsForm") DBColumnsForm dBColumnsForm,
            BindingResult bindingResult,
            @ModelAttribute("userSession") UserSession userSession,
            @ModelAttribute("originalDataFileForm") OriginalDataFileForm form,
            Model model) {
        model.addAttribute("columnType", DBColumn.ColumnType.values());

        if (bindingResult.hasErrors()) {
            return "custom";
        }

        try {
            // +++++++++++++++++++
            Resource  fileResource = new FileUrlResource(userSession.getTemporalFilePath());
            String insertSentence = null;

            String extension = getFileExtension(fileResource.getFilename());

            if(Objects.equals(extension, "xlsx") || Objects.equals(extension, "xls")){
                insertSentence = dbFileCreateService.callMakeInsertSentence(fileResource.getInputStream(), dBColumnsForm);
            }else if(Objects.equals(extension, "csv")){
                insertSentence = dbFileCreateService.callCsvMakeInsertSentence(fileResource.getInputStream(), dBColumnsForm);
            }

            model.addAttribute("insertSentence", insertSentence);
            model.addAttribute("dBColumnsForm", dBColumnsForm);
            return "dataFileView";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "タイムアウト！申し訳ありませんが最初からやり直してください。");
            return "home";
        }
    }

    @PostMapping(path = "create", params = "endCreate")
    public String endCreate(SessionStatus sessionStatus,
                            @ModelAttribute("userSession") UserSession userSession,
                            RedirectAttributes redirectAttributes,
                            UriComponentsBuilder uriComponentsBuilder) {

        if (userSession != null) {
            // セッション切れになっていない
            File temporalFile = new File(userSession.getTemporalFilePath());
            if (temporalFile.exists()) {
                temporalFile.delete();
            }
        }
        sessionStatus.setComplete();
        redirectAttributes.addFlashAttribute("message", "ご利用ありがとうございました!!");
        String redirectPath = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder)
                .withMappingName("DFCC#getHome").encode().build();
        return "redirect:" + redirectPath;
    }

    /**
     * Excelファイルから読みだされたHeader情報(headerMap)より
     * DBColumnオブジェクトをそれぞれ生成し {@link DBColumn}
     * {@link DBColumnsForm} の　{@link DBColumnsForm#getColumns()} に値を格納する処理
     *
     * @param form
     * @param headerMap
     */
    private void createDBColumnsForm(DBColumnsForm form, Map<Integer, String> headerMap) {
        headerMap.entrySet().stream().map(entry -> DBColumn.of(entry.getValue(), entry.getKey())).forEach(form::addColumns);
    }

    /**
     * マルチパートファイルの格納されているフィル名の拡張しを取得する
     * @param file multipartFile
     * @return file名の拡張子
     */
    private String getFileExtension(MultipartFile file){
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }

    /**
     * マルチパートファイルの格納されているフィル名の拡張しを取得する
     * @param fileName ファイル名
     * @return file名の拡張子
     */
    private String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(dotIndex + 1);
    }

//    @GetMapping("happen")
//    public String happen(){
//        throw new IllegalArgumentException("error occur");
//    }

}