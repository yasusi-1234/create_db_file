package com.example.create_db_file.from_zero.controller.form.parts;

import com.example.create_db_file.from_zero.controller.validator.TemporalConfirm;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@TemporalConfirm
public class DateTimeForm {

    @NotBlank(message = "この項目は必須です")
    private String columnName = "date_time";

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime minTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime maxTime = LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.MIN);

    @NotNull(message = "この項目は必須です")
    private TimeType timeType = TimeType.Between;

    private List<LocalDateTime> localDateTimes;

    public String getInsertLocaleDateTime(int index){
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        switch(this.timeType){
            case Between:
            case Rand:
                return sb.append(this.localDateTimes.get(index).format(formatter)).append("'").toString();
            case Fixed: return sb.append(this.minTime.format(formatter)).append("'").toString();
            case NullValue: return "NULL";
            default: return null;
        }
    }

    public String getDataLocaleDateTime(int index){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        switch(this.timeType){
            case Between:
            case Rand:
                return this.localDateTimes.get(index).format(formatter);
            case Fixed: return this.minTime.format(formatter);
            case NullValue: return "NULL";
            default: return null;
        }
    }
}
