package com.example.create_db_file.inquiry.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InquiryForm {

    @NotNull(message = "※ この項目は必須です")
    private InquiryCategory inquiryCategory = InquiryCategory.Bug;

    @Length(min = 1, max = 1000, message = "※ 1~1000文字以内で入力してください")
    private String contents;

    private LocalDateTime sendDateTime;

}
