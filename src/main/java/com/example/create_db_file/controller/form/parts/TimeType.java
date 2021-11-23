package com.example.create_db_file.controller.form.parts;

import lombok.Getter;

public enum TimeType {

    Between("間隔ランダム"),Rand("ランダム"), Fixed("固定値"), NullValue("全てNull");

    @Getter
    private final String viewName;

    TimeType(String viewName) {
        this.viewName = viewName;
    }
}
