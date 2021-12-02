package com.example.create_db_file.from_zero.domain.service;

import com.example.create_db_file.from_zero.controller.form.CreateFromZeroForm;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CreateFromZeroService {

    void createDummyUsersByLists(List<List<String>> requestUsers);

    String createInsertData(CreateFromZeroForm form);

    List<List<String>> createExcelData(CreateFromZeroForm form);
}
