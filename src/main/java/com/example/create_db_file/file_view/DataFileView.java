package com.example.create_db_file.file_view;

import com.example.create_db_file.from_file.domain.service.DbFileCreateServiceImpl;
import com.example.create_db_file.from_zero.controller.form.CreateFromZeroForm;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import com.example.create_db_file.from_file.controller.DbFileCreateController;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * ファイルダウンロード用クラス
 */
@Component("dataFileView")
@RequiredArgsConstructor
public class DataFileView extends AbstractView {

    private final ResourceLoader resourceLoader;

    /**
     * {@link DbFileCreateServiceImpl#makeInsertSentence(String, DBColumnsForm)}
     * より作成されたInsert文を{@link  DBColumnsForm#getFileName()}で指定されたfile名 + .sql形式のファイルとして
     * もし指定が無ければsample.sqlとして作成しダウンロードする
     * @param model コントローラークラス {@link DbFileCreateController} から渡されたModelオブジェクト
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) throws IOException {
        // dbのinsert文
        String insertSentence = (String) model.get("insertSentence");

        if(StringUtils.hasText(insertSentence)){
            renderInsertFile(model, insertSentence, response);
        }else {
            String fileName = (String) model.get("filename");
            String extension = (String) model.get("extension");
            renderSampleExcelFile(response, fileName, extension);
        }
    }

    private void renderInsertFile(Map<String, Object> model, String insertSentence, @NonNull HttpServletResponse response) throws IOException{
        DBColumnsForm form = (DBColumnsForm) model.get("dBColumnsForm");
        String fileName;

        if (form == null){
            CreateFromZeroForm createFromZeroForm = (CreateFromZeroForm) model.get("createFromZeroForm");
            fileName = StringUtils.hasText(createFromZeroForm.getFileName()) ? createFromZeroForm.getFileName() + ".sql" : "sample.sql";
        }else {
            fileName = StringUtils.hasText(form.getFileName()) ? form.getFileName() + ".sql" : "sample.sql";
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Type", "text/plain");

        StreamUtils.copy(new ByteArrayInputStream(insertSentence.getBytes(StandardCharsets.UTF_8)), response.getOutputStream());
    }

    private void renderSampleExcelFile(HttpServletResponse response, String filename, String extension) throws IOException{

        Resource resource = resourceLoader.getResource("classpath:sample_data/" + filename + "." + extension);

        if(!resource.exists()){
            resource = resourceLoader.getResource("classpath:sample_data/" + "employee.xlsx");
            response.setHeader("Content-Disposition", "attachment; filename=" + "employee.xlsx");
        }else{
            response.setHeader("Content-Disposition", "attachment; filename=" + filename + "." + extension);

        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        StreamUtils.copy(resource.getInputStream(), response.getOutputStream());

    }


}
