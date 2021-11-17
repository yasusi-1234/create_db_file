package com.example.create_db_file.controller.form;

import com.example.create_db_file.controller.validator.DBColumns;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * 読み取ったExcelファイルのヘッダー情報(DBのカラム情報)が格納されたフォームクラス
 */
@Data
public class DBColumnsForm {

    /** ダウンロードする際のファイル名 */
    @Pattern(regexp = "[\\w\\_\\-\\(\\)ぁ-んァ-ヶｱ-ﾝﾞﾟ一-龠]*", message = "※使用できない文字が含まれています")
    private String fileName;

    /** insert文に指定するテーブル名 必須 */
    @NotBlank(message = "※テーブル名を入力してください")
    private String tableName;

    /** columnのList {@link DBColumn} */
    @DBColumns
    private List<DBColumn> columns = new ArrayList<>();

    /**
     * フィールドのcolumnsに新しいcolumnオブジェクトを追加する
     * @param dbColumn 一つのカラム情報 {@link DBColumn}
     * @return 格納できたかの情報 {@code true} か {@code false}
     */
    public boolean addColumns(DBColumn dbColumn){
        return columns.add(dbColumn);
    }
}
