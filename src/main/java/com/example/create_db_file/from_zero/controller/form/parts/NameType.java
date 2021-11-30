package com.example.create_db_file.from_zero.controller.form.parts;

import lombok.Getter;

public enum NameType {

    Normal("漢字"),Kana("かな"),Roman("ローマ字"), NullValue("全てNull");

    @Getter
    private final String viewName;

    NameType(String viewName) {
        this.viewName = viewName;
    }
}
