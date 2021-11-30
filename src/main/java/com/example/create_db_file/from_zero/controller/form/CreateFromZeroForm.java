package com.example.create_db_file.from_zero.controller.form;

import com.example.create_db_file.from_zero.controller.validator.AllListConfirm;
import com.example.create_db_file.from_zero.controller.form.parts.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllListConfirm(field = "createSize")
public class CreateFromZeroForm implements Serializable {

    @Range(min = 1, max = 1000, message = "※数値は1~1000の間で指定可能です")
    private int createSize = 100;

    @NotBlank(message = "※この項目は必須です")
    private String tableName = "sample";

    @NotBlank(message = "※この項目は必須です")
    private String fileName = "sample";

    private List<@Valid FirstNameForm> firstNameForms = new ArrayList<>();

    private List<@Valid LastNameForm> lastNameForms = new ArrayList<>();

    private List<@Valid MailAddressForm> mailAddressForms = new ArrayList<>();

    private List<@Valid StringDataForm> stringDataForms = new ArrayList<>();

    private List<@Valid NumberForm> numberForms = new ArrayList<>();

    private List<@Valid DateTimeForm> dateTimeForms = new ArrayList<>();

    private List<@Valid DateForm> dateForms = new ArrayList<>();

    private List<@Valid TimeForm> timeForms = new ArrayList<>();

    public void addFirstNameForms() {
        firstNameForms.add(new FirstNameForm());
    }

    public void removeFirstNameForms(int index) {
        firstNameForms.remove(index);
    }

    public void addLastNameForms() {
        lastNameForms.add(new LastNameForm());
    }

    public void removeLastNameForms(int index) {
        lastNameForms.remove(index);
    }

    public void addMailAddressForms() {
        mailAddressForms.add(new MailAddressForm());
    }

    public void removeMailAddressForms(int index) {
        mailAddressForms.remove(index);
    }

    public void addStringDataForms() {
        stringDataForms.add(new StringDataForm());
    }

    public void removeStringDataForms(int index) {
        stringDataForms.remove(index);
    }

    public void addNumberForms() {
        numberForms.add(new NumberForm());
    }

    public void removeNumberForms(int index) {
        numberForms.remove(index);
    }

    public void addDateTimeForms() {
        dateTimeForms.add(new DateTimeForm());
    }

    public void removeDateTimeForms(int index) {
        dateTimeForms.remove(index);
    }

    public void addDateForms() {
        dateForms.add(new DateForm());
    }

    public void removeDateForms(int index) {
        dateForms.remove(index);
    }

    public void addTimeForms() {
        timeForms.add(new TimeForm());
    }

    public void removeTimeForms(int index) {
        timeForms.remove(index);
    }

    public List<Integer> numberIndexes() {
        if (CollectionUtils.isEmpty(numberForms)) {
            return new ArrayList<>();
        }
        int numberBeforeSize = firstNameForms.size() + lastNameForms.size() + mailAddressForms.size();
        int numberSize = numberForms.size();
        return Stream.iterate(numberBeforeSize, i -> i + 1).limit(numberSize).collect(Collectors.toList());
    }

    public int allRequestCount() {
        return firstNameForms.size() + lastNameForms.size() + mailAddressForms.size()
                + numberForms.size() + stringDataForms.size() + dateTimeForms.size()
                + dateForms.size() + timeForms.size();
    }

    public void removeField(String target, int targetIndex) {
        switch (target) {
            case "firstName":
                this.removeFirstNameForms(targetIndex);
                break;
            case "lastName":
                this.removeLastNameForms(targetIndex);
                break;
            case "mailAddress":
                this.removeMailAddressForms(targetIndex);
                break;
            case "number":
                this.removeNumberForms(targetIndex);
                break;
            case "string":
                this.removeStringDataForms(targetIndex);
                break;
            case "dateTime":
                this.removeDateTimeForms(targetIndex);
                break;
            case "date":
                this.removeDateForms(targetIndex);
                break;
            case "time":
                this.removeTimeForms(targetIndex);
        }
    }

    public String createInsertHeaderLeftSide() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(this.tableName).append(" (");
        this.lastNameForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.firstNameForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.mailAddressForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.numberForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.stringDataForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.dateTimeForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.dateForms.forEach(el -> sb.append(el.getColumnName()).append(", "));
        this.timeForms.forEach(el -> sb.append(el.getColumnName()).append(", "));

        sb.setLength(sb.length() - 2);
        sb.append(") VALUES(");

        return sb.toString();
    }

    public List<String> createHeaderList() {
        List<String> list = new ArrayList<>();

        this.lastNameForms.forEach(el -> list.add(el.getColumnName()));
        this.firstNameForms.forEach(el -> list.add(el.getColumnName()));
        this.mailAddressForms.forEach(el -> list.add(el.getColumnName()));
        this.numberForms.forEach(el -> list.add(el.getColumnName()));
        this.stringDataForms.forEach(el -> list.add(el.getColumnName()));
        this.dateTimeForms.forEach(el -> list.add(el.getColumnName()));
        this.dateForms.forEach(el -> list.add(el.getColumnName()));
        this.timeForms.forEach(el -> list.add(el.getColumnName()));
        return list;
    }

}
