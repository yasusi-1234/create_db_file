package com.example.create_db_file.controller.form.parts;

import com.example.create_db_file.controller.form.CreateFromZeroForm;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AddForm {

    private Map<String, Integer> addMap = new LinkedHashMap<>();

    public AddForm(){
        addMap.put("lastName", 0);
        addMap.put("firstName", 0);
        addMap.put("mailAddress", 0);
        addMap.put("number", 0);
        addMap.put("string", 0);
        addMap.put("dateTime", 0);
        addMap.put("date", 0);
        addMap.put("time", 0);
    }

    public void initializeValue(){
        this.addMap.entrySet().forEach(e -> e.setValue(0));
    }

    public void addCreateZeroForm(CreateFromZeroForm createFromZeroForm){
        this.addMap.entrySet().stream().filter(e -> e.getValue() > 0).forEach(entry -> {
            int loopCount = entry.getValue() > 4 ? 4 : entry.getValue();
            switch (entry.getKey()){
                case "firstName" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addFirstNameForms();
                    }
                break;
                case "lastName" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addLastNameForms();
                    }
                break;
                case "mailAddress" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addMailAddressForms();
                    }
                break;
                case "string" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addStringDataForms();
                    }
                break;
                case "number" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addNumberForms();
                    }
                break;
                case "dateTime" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addDateTimeForms();
                    }
                break;
                case "date" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addDateForms();
                    }
                break;
                case "time" :
                    for (int i = 0; i < loopCount; i++) {
                        createFromZeroForm.addTimeForms();
                    }
                break;
            }
        });
    }

}
