package com.example.create_db_file.domain.service.excel;

import com.example.create_db_file.controller.form.DBColumn;
import com.example.create_db_file.controller.form.DBColumnsForm;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExcelInformationReader {

    /**
     * Excelデータのヘッダー部分を読み込み値と位置を読み取り
     * Mapオブジェクトに情報を格納して返却する
     * なおヘッダー部分が未設定・データ部分が未設定の場合は処理が継続できない為
     * 空のMapを返却する
     * @param in Excelファイルのデータstream
     * @return Key:ヘッダーの位置 Value:ヘッダーの値を格納したMap
     */
    public Map<Integer, String> analyzeHeader(InputStream in){
        Map<Integer, String> headerMap = new LinkedHashMap<>();
        try(Workbook workbook = WorkbookFactory.create(in)){

            Sheet sheet = workbook.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum();
            Row row = sheet.getRow(firstRow);
            Row dataRow = sheet.getRow(firstRow + 1);

            if(row == null || dataRow == null){
                // ヘッダー部分データ部分のデータの書き込みが無い場合
                return headerMap;
            }

            for(int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++){
                String headerValue = getConversionCellValue(row.getCell(i));
                if(!StringUtils.hasText(headerValue)){
                    // 取得した値が空
                    continue;
                }
                int columnIndex = row.getCell(i).getColumnIndex();
                headerMap.put(columnIndex, headerValue);
            }
            return headerMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headerMap;
    }

    /**
     * Excel用stream情報からExcelファイルを第二引数で与えられたPathに保存する
     * @param in Excelファイルのデータstream
     * @param newFilePath 保存するPath
     */
    public void saveExcelFile(InputStream in, String newFilePath){
        try(Workbook workbook = WorkbookFactory.create(in);
            FileOutputStream out = new FileOutputStream(newFilePath)
        ){
            workbook.write(out);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Excel用stream情報とform情報からinsert文を作成し、String型で返却するメソッド
     * @param in Excel用stream情報
     * @param form 作成したいInsert文の情報Object
     * @return Excelファイル情報から作成したinsert文の文字列
     */
    public String makeInsertSentence(InputStream in, DBColumnsForm form){
        try(Workbook workbook = WorkbookFactory.create(in)){
            Sheet sheet = workbook.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum() + 1;
            int lastRow = rowExistPosition(sheet);

            int headerRowSize = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum();

            Row row = sheet.getRow(firstRow);
            if(row == null){
                return "";
            }
            String leftTemplate = makeInsertTemplateLeft(form);
            int firstColumn =  row.getFirstCellNum();
            StringBuilder sb = new StringBuilder();
            List<Integer> needColumnIndex = form.getColumns().stream().filter(DBColumn::isInclude).map(DBColumn::getColumnIndex).collect(Collectors.toList());
            List<DBColumn> dbColumns = form.getColumns();

            for(int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++){
                Row moveRow = sheet.getRow(rowIndex);
                sb.append(leftTemplate);

                // moveRow.getLastCellNum();
                for(int columnIndex = firstColumn; columnIndex < headerRowSize; columnIndex++){
                    if(!needColumnIndex.contains(columnIndex)){
                        continue;
                    }
                    String cellValue = getConversionCellValue(moveRow.getCell(columnIndex));

                    if(dbColumns.get(columnIndex).getType() == DBColumn.ColumnType.STRING){
                        sb.append("'").append(cellValue).append("', ");
                    }else if(dbColumns.get(columnIndex).getType() == DBColumn.ColumnType.NUMBER){
                        sb.append(cellValue).append(", ");
                    }else if(dbColumns.get(columnIndex).getType() == DBColumn.ColumnType.NULL){
                        sb.append("NULL").append(", ");
                    }
                }
                sb.delete(sb.length() - 2, sb.length()).append(");").append(System.lineSeparator());
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Insert文の 'insert into (??, ??, ??) values('までの左側の部分を
     * form情報から作成し返却する
     * formから使う情報は,テーブル名・カラム名・変更したいカラム名・情報を含めるかどうか
     * @param form 作成したいInsert文の指定情報等が含まれるクラス 参考:{@link com.example.create_db_file.controller.form.DBColumnsForm}
     * @return form情報から作成したinsert分の左側の部分
     */
    private String makeInsertTemplateLeft(DBColumnsForm form){
        String columnName = form.getColumns().stream().filter(DBColumn::isInclude)
                .map(col -> StringUtils.hasText(col.getChangeColumnName()) ? col.getChangeColumnName() : col.getColumnName())
                .collect(Collectors.joining(", "));
        return "INSERT INTO " + form.getTableName() + " (" + columnName + ") VALUES(";

    }

    /**
     * 取得したExcelのセル情報から値を取得し、String値に変換して返却する
     *
     * @param cell セル情報
     * @return セル情報から得た情報をString値で返却 空やnullの場合は空文字のString値を返却
     */
    private static String getConversionCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                int returnVal = (int) cell.getNumericCellValue();
                return String.valueOf(returnVal);
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            case ERROR:
                return "";
        }
        return "";
    }

    /**
     * Poiの仕様上sheet.getLastRowNumメソッドは、
     * Excelの行に書き込みを行いそのあとその行をクリアした場合でも、存在する行としてカウントしてしまう
     * バグの原因となってしまうので、実際に書き込みのある行を調べた上で返却を行うためのメソッド
     *
     * @param sheet ExcelのシートObject
     * @return 実際に値が書き込まれている最終行の値
     */
    private int rowExistPosition(Sheet sheet) {
        // poiにより算出された最終行
        int rowNumOfPoi = sheet.getLastRowNum();
        Row row = sheet.getRow(rowNumOfPoi);
        // poiにより算出された最終列
        int columnNumOfPoi = row.getLastCellNum();
        // 返却値
        int returnVal = rowNumOfPoi;
        for (int indexRow = rowNumOfPoi; true; indexRow--) {
            Row moveRow = sheet.getRow(indexRow);
            // ループのフラグ
            boolean breakFlag = false;
            for (int columnIndex = 0; columnIndex < columnNumOfPoi; columnIndex++) {
                String cellValue = getConversionCellValue(moveRow.getCell(columnIndex));
                if (!cellValue.isBlank()) {
                    breakFlag = true;
                    returnVal = indexRow;
                    break;
                }
            }
            if (breakFlag) {
                break;
            }
        }
        return returnVal;
    }
}
