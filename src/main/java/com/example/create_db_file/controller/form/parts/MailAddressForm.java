package com.example.create_db_file.controller.form.parts;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class MailAddressForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName;

    private Integer accountLength;

    private String domainName;

    private List<String> mailAddresses;

    public String getInsertMailAddress(int index){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        return sb.append(this.mailAddresses.get(index)).append("'").toString();
    }

    public String getDataMailAddress(int index){
        return this.mailAddresses.get(index);
    }

}
