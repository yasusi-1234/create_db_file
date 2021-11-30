package com.example.create_db_file.from_zero.controller.form.parts;

import com.example.create_db_file.from_zero.controller.validator.StringConfirm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@StringConfirm
public class StringDataForm {
    @NotBlank(message = "この項目は必須です")
    private String columnName = "string_data";

    private String fixedString = "固定値";

    private int minLength = 10;

    private int maxLength = 100;

    @NotNull(message = "この項目は必須です")
    private StringType stringType = StringType.Rand;

    private List<String> stringList;

    public String getInsertString(int index){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        switch (this.stringType){
            case Between:
            case Rand:
            case UUID:
                return sb.append(this.stringList.get(index)).append("'").toString();
            case Fixed: return sb.append(this.fixedString).append("'").toString();
            case NullValue: return "NULL";
            default: return null;
        }
    }

    public String getDataString(int index){
        switch (this.stringType){
            case Between:
            case Rand:
            case UUID:
                return this.stringList.get(index);
            case Fixed:
                return this.fixedString;
            case NullValue: return "NULL";
            default: return null;
        }
    }
}
