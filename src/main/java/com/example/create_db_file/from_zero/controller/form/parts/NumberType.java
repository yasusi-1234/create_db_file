package com.example.create_db_file.from_zero.controller.form.parts;

import lombok.Getter;

public enum NumberType {

    Between("間隔ランダム"),Rand("ランダム"), Fixed("固定値"), Serial("連番"), NullValue("全てNull");

    @Getter
    private final String viewName;

    NumberType(String viewName) {
        this.viewName = viewName;
    }
}
