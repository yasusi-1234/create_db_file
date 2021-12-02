package com.example.create_db_file.from_file.controller;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.controller.form.OriginalDataFileForm;
import com.example.create_db_file.session.UserSession;
import com.example.create_db_file.from_file.domain.service.DbFileCreateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Map<Integer, String> resultMap = dbFileCreateService.findHeader(form.getMultipartFile());

        if (resultMap.isEmpty()) {
            // ヘッダーの値が設定されていない、またはデータ部分が無い場合(処理できない為)
            model.addAttribute("noHeader",
                    "※ヘッダーの値が読み取れないか、あるいはデータが情報が不足しています。ファイルを確認してください。");
            return "home";
        }

        String temporalFileName = dbFileCreateService.fileToSaveTemporarily(form.getMultipartFile());

        if (Objects.nonNull(userSession.getTemporalFilePath())
                && Files.exists(Path.of(userSession.getTemporalFilePath()))) {
            // セッションに既にファイルがpathが格納されている、かつファイルも存在する
            try {
                Files.delete(Path.of(userSession.getTemporalFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("noHeader",
                        "※予期しないエラーが発生しました。申し訳ありませんがやり直してください。");
                return "home";
            }
        }

        userSession.setTemporalFilePath(temporalFileName);

        String redirectPath = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder)
                .withMappingName("DFCC#getUpload").encode().build();

        return "redirect:" + redirectPath;
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

        Map<Integer, String> resultMap = dbFileCreateService.findHeader(userSession.getTemporalFilePath(), dbColumnsForm);

        if(CollectionUtils.isEmpty(resultMap)){
            model.addAttribute("noHeader",
                    "※予期しないエラーが発生しました。申し訳ありませんがやり直してください。");
            return "home";
        }else{
            model.addAttribute("columnType", DBColumn.ColumnType.values());
            return "custom";
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
            String insertSentence = dbFileCreateService.makeInsertSentence(userSession.getTemporalFilePath(), dBColumnsForm);
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

}
