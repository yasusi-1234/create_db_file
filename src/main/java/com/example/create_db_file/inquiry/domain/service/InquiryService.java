package com.example.create_db_file.inquiry.domain.service;

import com.example.create_db_file.inquiry.domain.model.Inquiry;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryService {

    Inquiry saveInquiry(String contents, int inquiryId);

    List<Inquiry> findAllInquiryForYesterday(LocalDateTime dateTime);
}
