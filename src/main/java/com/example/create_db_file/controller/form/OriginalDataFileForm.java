package com.example.create_db_file.controller.form;


import com.example.create_db_file.controller.validator.ExpectedFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excelファイル添付用のフォームクラス
 */
@Data
public class OriginalDataFileForm {

    @ExpectedFile
    private MultipartFile multipartFile;

}
