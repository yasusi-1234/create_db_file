package com.example.create_db_file.controller.form.parts;

import com.example.create_db_file.domain.service.dummy.TemporaryDummyUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LastNameForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName;
    @NotNull(message = "この項目は必須です")
    private NameType nameType = NameType.Normal;

    public String getInsertString(TemporaryDummyUser dummyUser){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        switch (this.nameType){
            case Normal: return sb.append(dummyUser.getLastName()).append("'").toString();
            case Kana: return sb.append(dummyUser.getLastNameKana()).append("'").toString();
            case Roman: return sb.append(dummyUser.getLastNameRoman()).append("'").toString();
            case NullValue: return "NULL";
            default: return "";
        }
    }

    public String getDataString(TemporaryDummyUser dummyUser){
        switch (this.nameType){
            case Normal: return dummyUser.getLastName();
            case Kana: return dummyUser.getLastNameKana();
            case Roman: return dummyUser.getLastNameRoman();
            case NullValue: return "NULL";
            default: return "";
        }
    }
}
