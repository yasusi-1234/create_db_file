package com.example.create_db_file.from_zero.controller.form.parts;

import com.example.create_db_file.from_zero.controller.validator.TemporalConfirm;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@TemporalConfirm
public class DateForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName = "date";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate minTime = LocalDate.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate maxTime = LocalDate.now().plusMonths(1);

    @NotNull(message = "この項目は必須です")
    private TimeType timeType = TimeType.Between;

    private List<LocalDate> localDates;

    public String getInsertDateTime(int index){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        switch(this.timeType){
            case Between:
            case Rand:
                return sb.append(this.localDates.get(index).format(formatter)).append("'").toString();
            case Fixed: return sb.append(this.minTime.format(formatter)).append("'").toString();
            case NullValue: return "NULL";
            default: return null;
        }
    }

    public String getDataDateTime(int index){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        switch(this.timeType){
            case Between:
            case Rand:
                return this.localDates.get(index).format(formatter);
            case Fixed: return this.minTime.format(formatter);
            case NullValue: return "NULL";
            default: return null;
        }
    }
}
