package com.example.create_db_file.inquiry.controller.form;

import lombok.Getter;

public enum InquiryCategory {

    Bug(1, "バグ"), Request(2, "リクエスト"), Others(3, "その他");
    @Getter
    private final String viewName;
    @Getter
    private final int index;

    InquiryCategory(int index, String viewName) {
        this.index = index;
        this.viewName = viewName;
    }
}
