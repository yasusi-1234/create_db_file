package com.example.create_db_file.controller.session;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSession implements Serializable {

    private String temporalFilePath;
}
