package com.example.create_db_file.from_file.domain.file_helper;

public interface FileHelperFactory {

    FileInformationHelper createFileInformationHelper(String filename);
}
