package com.example.create_db_file.controller.form.parts;

import lombok.Getter;

public enum StringType {

    Between("間隔ランダム"), Rand("ランダム"), UUID("UUID"), Fixed("固定値"), NullValue("全てNull");

    @Getter
    private String viewName;

    StringType(String viewName) {
        this.viewName = viewName;
    }
}
