package com.example.create_db_file.file_view;

import com.example.create_db_file.from_zero.controller.form.CreateFromZeroForm;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component("excelFileView")
public class ExcelFileView extends AbstractXlsxView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response){

        List<List<String>> excelData = (List<List<String>>) model.get("excelData");
        CreateFromZeroForm form = (CreateFromZeroForm) model.get("createFromZeroForm");



        Sheet sheet = workbook.createSheet(form.getTableName());

        int columnSize = form.allRequestCount();
        List<String> headers = excelData.get(0);

        // ヘッダーの生成
        Row row = sheet.createRow(0);
        for (int i = 0; i < columnSize; i++) {
            row.createCell(i).setCellValue(headers.get(i));
        }

        List<Integer> numberIndexes = form.numberIndexes();

        // ボディー作成
        for (int i = 0; i < form.getCreateSize(); i++) {
            row = sheet.createRow(i + 1);
            List<String> data = excelData.get(i + 1);
            for (int j = 0; j < columnSize; j++) {
                if (numberIndexes.contains(j)){
                    row.createCell(j).setCellValue(Long.parseLong(data.get(j)));
                }else{
                    row.createCell(j).setCellValue(data.get(j));
                }
            }
        }

        // カラムのサイズ調整
        for (int i = 0; i < columnSize; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + form.getFileName() + ".xlsx");
    }
}
