package com.example.create_db_file.from_file.domain.file_helper;

import org.springframework.lang.NonNull;

/**
 * ファイルの拡張子別のタイプを表すenumクラス
 */
public enum ExtensionType {

    Excel, Csv, Other;

    /**
     * 拡張子付きのファイル名からマッチした {@link ExtensionType} を返却する
     * @param fileName 拡張子付きのファイル名
     * @return 引数で受け取ったファイル名にマッチした {@link ExtensionType}
     */
    public static ExtensionType getExtensionType(@NonNull String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex == -1){
            return Other;
        }

        String extension = fileName.substring(dotIndex + 1);

        return getExtensionTypeFromExtension(extension);
    }

    /**
     * 拡張子の文字列からマッチした {@link ExtensionType} を返却する
     * @param extension ファイルの拡張子
     * @return 引数で受け取った文字列にマッチした {@link ExtensionType}
     */
    public static ExtensionType getExtensionTypeFromExtension(@NonNull String extension){
        switch (extension){
            case "xlsx":
            case "xls":
                return Excel;
            case "csv":
                return Csv;
            default:
                return Other;
        }
    }
}
