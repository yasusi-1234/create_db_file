package com.example.create_db_file.controller;

import com.example.create_db_file.controller.form.DBColumn;
import com.example.create_db_file.controller.form.DBColumnsForm;
import com.example.create_db_file.controller.form.OriginalDataFileForm;
import com.example.create_db_file.controller.session.UserSession;
import com.example.create_db_file.service.DbFileCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@SessionAttributes(types = {UserSession.class})
public class DbFileCreateController {

    @ModelAttribute("userSession")
    public UserSession userSession(){
        return new UserSession();
    }

    private final DbFileCreateService dbFileCreateService;

    @GetMapping("/home")
    public String getHome(
            @ModelAttribute("originalDataFileForm")OriginalDataFileForm form,
                          Model model){
        return "home";
    }

    @PostMapping("/upload")
    public String postUpload(
            @Validated @ModelAttribute("originalDataFileForm")OriginalDataFileForm form,
            BindingResult bindingResult,
            Model model,
            @ModelAttribute("userSession")UserSession userSession,
            @ModelAttribute("dBColumnsForm")DBColumnsForm dbColumnsForm){
        if(bindingResult.hasErrors()){
            return "home";
        }

        try(InputStream in = form.getMultipartFile().getInputStream()){
            Map<Integer, String> resultMap = dbFileCreateService.findHeader(in);
            System.out.println(resultMap);
            if(resultMap.isEmpty()){
                // ヘッダーの値が設定されていない、またはデータ部分が無い場合(処理できない為)
                model.addAttribute("noHeader",
                        "ヘッダーの値が読み取れないか、あるいはデータが情報が不足しています。ファイルを確認してください。");
                return "home";
            }

            // ヘッダーの値が正常だった場合の処理
            // ファイルを一時保存する処理
            String temporalFileName = dbFileCreateService
                    .fileToSaveTemporarily(form.getMultipartFile().getInputStream(),
                            form.getMultipartFile().getOriginalFilename());
            System.out.println(temporalFileName);

            if(Objects.nonNull(userSession.getTemporalFilePath())
            && Files.exists(Path.of(userSession.getTemporalFilePath()))){
                // セッションに既にファイルがpathが格納されている、かつファイルも存在する
                Files.delete(Path.of(userSession.getTemporalFilePath()));
            }

            userSession.setTemporalFilePath(temporalFileName);
            // 新しいFormモデルに値を格納する処理
            createDBColumnsForm(dbColumnsForm, resultMap);
            model.addAttribute("columnType", DBColumn.ColumnType.values());

            return "custom";
        }catch (IOException e){
            e.printStackTrace();
        }
        return "home";
    }

    @PostMapping(path = "create", params = "create")
    public String createRequest(
            @Validated @ModelAttribute("dBColumnsForm")DBColumnsForm dBColumnsForm,
            BindingResult bindingResult,
            @ModelAttribute("userSession")UserSession userSession,
            Model model){
        model.addAttribute("columnType", DBColumn.ColumnType.values());

        if(bindingResult.hasErrors()){
            return "custom";
        }
        System.out.println(dBColumnsForm);
        Resource fileResource = null;
        String insertSentence = null;
        try{
            fileResource = new FileUrlResource(userSession.getTemporalFilePath());
            insertSentence = dbFileCreateService.callMakeInsertSentence(fileResource.getInputStream(),dBColumnsForm);

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
                            UriComponentsBuilder uriComponentsBuilder){

        if(userSession != null){
            // セッション切れになっていない
            File temporalFile = new File(userSession.getTemporalFilePath());
            if(temporalFile.exists()){
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
     * @param form
     * @param headerMap
     */
    private void createDBColumnsForm(DBColumnsForm form, Map<Integer, String> headerMap){
        headerMap.entrySet().stream().map(entry -> DBColumn.of(entry.getValue(), entry.getKey())).forEach(form::addColumns);
    }

}
