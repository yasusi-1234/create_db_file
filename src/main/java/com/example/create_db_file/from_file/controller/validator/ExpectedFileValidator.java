package com.example.create_db_file.from_file.controller.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ExpectedFileValidator implements ConstraintValidator<ExpectedFile, MultipartFile> {

    private String message;

    @Override
    public void initialize(ExpectedFile constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    /**
     * ファイル情報がExcelファイル形式(.xlsx, .xls)のファイルか確認し
     * Excel形式のファイルだった場合は正常値 {@code true}
     * 他の形式のファイルだった場合は異常値 {@code false}　を返却
     * @param file 添付されたファイル情報
     * @param context
     * @return true(正常値) or false(異常値)
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(file == null || file.isEmpty()){
            message = "※ ファイルが選択されていません。";
            // fileがnull
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        String contentType = file.getContentType();
        // .xlsx 形式のコンテントタイプ名
        String xlsxContent = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        // .xls 形式のコンテントタイプ名
        String xlsContent = "application/vnd.ms-excel";
        // .csv 形式のコンテントタイプ
        String csvContent = "text/csv";
        if(Objects.equals(contentType, xlsxContent)
        || Objects.equals(contentType, xlsContent)
        || Objects.equals(contentType, csvContent)){
            return true;
        }else{
            message = "※ サポートされていないファイル形式です。";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
    }

}
