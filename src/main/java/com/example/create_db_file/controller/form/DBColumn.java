package com.example.create_db_file.controller.form;

import lombok.Data;

/**
 * Excelファイルから読み取ったヘッダー情報の(DBのカラム情報)一つのを表すクラス
 */
@Data
public class DBColumn {
    /** カラム名 */
    private String columnName;
    /** Excel上のカラム名の列の位置 */
    private Integer columnIndex;
    /** 変更したいカラム名 */
    private String changeColumnName;
    /** DB上のデータタイプ指定 {@link ColumnType} */
    private ColumnType type = ColumnType.STRING;
    /** insert文にカラムを含めるかの情報 defaultはtrue */
    private boolean include = true;

    /**
     *
     * @param columnName カラム名(Excel上の)
     * @param columnIndex カラム位置(Excel上の)
     * @return columnName 及び columnIndexから作成されるDBColumnオブジェクト
     */
    public static DBColumn of(String columnName, Integer columnIndex){
        DBColumn column = new DBColumn();
        column.setColumnName(columnName);
        column.setColumnIndex(columnIndex);
        return column;
    }

    /**
     * DB上で使われるデータの簡易オプション
     * Insert分生成のvaluesのデータ部分の作成時に使われる
     * String -> '指定文字','3' Number・Decimal -> 3, 3.0
     */
    public enum ColumnType{ STRING, NUMBER, DECIMAL }
}
