package com.example.create_db_file.controller.create;

import com.example.create_db_file.controller.form.DBColumnsForm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * {@link com.example.create_db_file.service.DbFileCreateServiceImpl#callMakeInsertSentence(InputStream, DBColumnsForm)}
     * より作成されたInsert文を{@link  DBColumnsForm#getFileName()}で指定されたfile名 + .sql形式のファイルとして
     * もし指定が無ければsample.sqlとして作成しダウンロードする
     * @param model コントローラークラス {@link com.example.create_db_file.controller.DbFileCreateController} から渡されたModelオブジェクト
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // dbのinsert文
        String insertSentence = (String) model.get("insertSentence");

        if(StringUtils.hasText(insertSentence)){
            renderInsertFile(model, insertSentence, response);
        }else {
            renderSampleExcelFile(response);
        }
    }

    private void renderInsertFile(Map<String, Object> model, String insertSentence, HttpServletResponse response) throws IOException{
        DBColumnsForm form = (DBColumnsForm) model.get("dBColumnsForm");

        String fileName = StringUtils.hasText(form.getFileName()) ? form.getFileName() + ".sql" : "sample.sql";

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Type", "text/plain");

        StreamUtils.copy(new ByteArrayInputStream(insertSentence.getBytes(StandardCharsets.UTF_8)), response.getOutputStream());
    }

    private void renderSampleExcelFile(HttpServletResponse response) throws IOException{

        Resource resource = resourceLoader.getResource("classpath:sample_data/employee.xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=" + "employee.xlsx");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        StreamUtils.copy(resource.getInputStream(), response.getOutputStream());
    }


}
