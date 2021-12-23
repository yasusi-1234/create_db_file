package com.example.create_db_file.from_file.domain.file_helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FileHelperFactoryImpl implements FileHelperFactory{

    private final FileInformationHelper excelHelper;
    private final FileInformationHelper csvHelper;
    private final FileInformationHelper noneHelper;

    @Autowired
    public FileHelperFactoryImpl(
            @Qualifier("excelInformationHelper") FileInformationHelper excelHelper,
            @Qualifier("csvInformationHelper") FileInformationHelper csvHelper,
            @Qualifier("noneHelper") FileInformationHelper noneHelper
    ) {
        this.excelHelper = excelHelper;
        this.csvHelper = csvHelper;
        this.noneHelper = noneHelper;
    }

    @Override
    public FileInformationHelper createFileInformationHelper(String filename) {
        ExtensionType extensionType = ExtensionType.getExtensionType(filename);
        switch (extensionType) {
            case Excel:
                return excelHelper;
            case Csv:
                return csvHelper;
            case Other:
            default:
                return noneHelper;
        }
    }
}
