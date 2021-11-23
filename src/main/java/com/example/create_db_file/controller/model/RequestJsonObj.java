package com.example.create_db_file.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestJsonObj implements Serializable {

    private String err;
    private List<List<String>> name;
}
