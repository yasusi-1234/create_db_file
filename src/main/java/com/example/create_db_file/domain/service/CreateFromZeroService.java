package com.example.create_db_file.domain.service;

import com.example.create_db_file.controller.form.CreateFromZeroForm;
import com.example.create_db_file.domain.model.DummyUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CreateFromZeroService {
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    void createDummyUsersByLists(List<List<String>> requestUsers);

    String createInsertData(CreateFromZeroForm form);

    List<List<String>> createExcelData(CreateFromZeroForm form);

//    List<DummyUser> createDummyUsersByLists(List<List<String>> requestUsers);
}
