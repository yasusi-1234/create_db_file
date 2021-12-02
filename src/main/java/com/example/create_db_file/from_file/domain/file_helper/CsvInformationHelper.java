package com.example.create_db_file.from_file.domain.file_helper;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.DBColumnsForm;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component("csvInformationHelper")
public class CsvInformationHelper implements FileInformationHelper {

    /**
     * InputStreamからヘッダーの値を読み込み返却する。なおヘッダーとデータが無い場合・ヘッダーのみの
     * データの場合空のMapが返却される
     * @param in inputStream
     * @return inputStreamから読み取ったヘッダーのマップ
     */
    @Override
    public Map<Integer, String> analyzeHeader(InputStream in){
        Map<Integer, String> headerMap = new LinkedHashMap<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))){

            String firstLine = br.readLine();
            String nextLine = br.readLine();

            if (Objects.isNull(firstLine) || Objects.isNull(nextLine)){
                return headerMap;
            }
            String[] headers = firstLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(i, headers[i]);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return headerMap;
    }

    @Override
    public void saveFile(InputStream in, String newFilePath){
        try(FileOutputStream out = new FileOutputStream(newFilePath)){
            StreamUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String makeInsertSentence(InputStream in, DBColumnsForm form){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))){
            // headerスキップ
            br.readLine();

            String leftTemplate = makeInsertTemplateLeft(form);
            List<Integer> needColumnIndex = form.getColumns().stream().filter(DBColumn::isInclude).map(DBColumn::getColumnIndex).collect(Collectors.toList());
            List<DBColumn> dbColumns = form.getColumns();
            StringBuilder sb = new StringBuilder();

            br.lines().forEach(line -> {

                List<String> lines = new ArrayList<>(Arrays.asList(line.split(",")));
                if(line.endsWith(",")){
                    lines.add("");
                }

                sb.append(leftTemplate);
                for (int i = 0; i < lines.size(); i++) {
                    if(!needColumnIndex.contains(i)){
                        continue;
                    }

                    String insertValue = lines.get(i);

                    if(dbColumns.get(i).getType() == DBColumn.ColumnType.STRING){
                        sb.append("'").append(insertValue).append("', ");
                    }else if(dbColumns.get(i).getType() == DBColumn.ColumnType.NUMBER){
                        sb.append(insertValue).append(", ");
                    }else if(dbColumns.get(i).getType() == DBColumn.ColumnType.NULL){
                        sb.append("NULL").append(", ");
                    }
                }

                sb.delete(sb.length() - 2, sb.length()).append(");").append(System.lineSeparator());

            });

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
     * @param form 作成したいInsert文の指定情報等が含まれるクラス 参考:{@link DBColumnsForm}
     * @return form情報から作成したinsert分の左側の部分
     */
    private String makeInsertTemplateLeft(DBColumnsForm form){
        String columnName = form.getColumns().stream().filter(DBColumn::isInclude)
                .map(col -> StringUtils.hasText(col.getChangeColumnName()) ? col.getChangeColumnName() : col.getColumnName())
                .collect(Collectors.joining(", "));
        return "INSERT INTO " + form.getTableName() + " (" + columnName + ") VALUES(";

    }
}
