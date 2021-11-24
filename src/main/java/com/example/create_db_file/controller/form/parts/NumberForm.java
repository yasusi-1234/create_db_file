package com.example.create_db_file.controller.form.parts;

import com.example.create_db_file.controller.form.parts.validator.NumberConfirm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NumberConfirm
public class NumberForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName = "id";

    private long minNumber = 1;

    private long maxNumber = 100;

    @NotNull(message = "この項目は必須です")
    private NumberType numberType = NumberType.Between;

    private List<Long> numbers;

    public String getInsertNumber(int index){
        switch (this.numberType){
            case Between:
            case Rand:
            case Serial:
                return this.numbers.get(index).toString();
            case Fixed: return String.valueOf(this.minNumber);
            case NullValue: return "NULL";
            default: return null;
        }
    }
}
