package com.example.create_db_file.from_zero.controller.form.parts;

import com.example.create_db_file.from_zero.domain.model.mapper.TemporaryDummyUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FirstNameForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName = "first_name";
    @NotNull(message = "この項目は必須です")
    private NameType nameType = NameType.Normal;

    public String getInsertString(TemporaryDummyUser dummyUser){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        switch (this.nameType){
            case Normal: return sb.append(dummyUser.getFirstName()).append("'").toString();
            case Kana: return sb.append(dummyUser.getFirstNameKana()).append("'").toString();
            case Roman: return sb.append(dummyUser.getFirstNameRoman()).append("'").toString();
            case NullValue: return "NULL";
            default: return "";
        }
    }

    public String getDataString(TemporaryDummyUser dummyUser){
        switch (this.nameType){
            case Normal: return dummyUser.getFirstName();
            case Kana: return dummyUser.getFirstNameKana();
            case Roman: return dummyUser.getFirstNameRoman();
            case NullValue: return "NULL";
            default: return "";
        }
    }
}
