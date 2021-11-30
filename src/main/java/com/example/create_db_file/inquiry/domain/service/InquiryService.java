package com.example.create_db_file.inquiry.domain.service;

import com.example.create_db_file.inquiry.domain.model.Inquiry;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryService {
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    Inquiry saveInquiry(String contents, int inquiryId);

    List<Inquiry> findAllInquiryForYesterday(LocalDateTime dateTime);
}
