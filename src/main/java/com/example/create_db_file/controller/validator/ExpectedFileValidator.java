package com.example.create_db_file.controller.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ExpectedFileValidator implements ConstraintValidator<ExpectedFile, MultipartFile> {
    @Override
    public void initialize(ExpectedFile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
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
        if(file == null){
            // fileがnull
            return false;
        }

        String contentType = file.getContentType();
        // .xlsx 形式のコンテントタイプ名
        String xlsxContent = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        // .xls 形式のコンテントタイプ名
        String xlsContent = "application/vnd.ms-excel";
        if(Objects.equals(contentType, xlsxContent)
        || Objects.equals(contentType, xlsContent)){
            return true;
        }
        return false;
    }
}
