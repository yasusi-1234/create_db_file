package com.example.create_db_file.inquiry.controller;

import com.example.create_db_file.inquiry.domain.model.Inquiry;
import com.example.create_db_file.inquiry.domain.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SampleRestController {

    private final InquiryService inquiryService;

    @GetMapping
    public List<Inquiry> test(){
        List<Inquiry> result = inquiryService.findAllInquiryForYesterday(LocalDateTime.now().plusDays(1));
        System.out.println(result);
        return result;
    }

}
