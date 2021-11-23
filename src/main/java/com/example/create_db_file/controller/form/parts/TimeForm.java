package com.example.create_db_file.controller.form.parts;

import com.example.create_db_file.controller.form.parts.validator.TemporalConfirm;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@TemporalConfirm
public class TimeForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime minTime = LocalTime.MIN;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime maxTime = LocalTime.MAX;

    @NotNull(message = "この項目は必須です")
    private TimeType timeType = TimeType.Between;

    private List<LocalTime> localTimes;

    public String getInsertDateTime(int index){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        switch(this.timeType){
            case Between:
            case Rand:
                return sb.append(this.localTimes.get(index).format(formatter)).append("'").toString();
            case Fixed: return sb.append(this.minTime.format(formatter)).append("'").toString();
            case NullValue: return "NULL";
            default: return null;
        }
    }

    public String getDataDateTime(int index){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        switch(this.timeType){
            case Between:
            case Rand:
                return this.localTimes.get(index).format(formatter);
            case Fixed: return this.minTime.format(formatter);
            case NullValue: return "NULL";
            default: return null;
        }
    }
}
