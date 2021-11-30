package com.example.create_db_file.inquiry.domain.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class InquiryCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inquiryCategoryId;

    private String categoryName;

    public static InquiryCategory of(Integer id){
        InquiryCategory inquiryCategory = new InquiryCategory();
        inquiryCategory.setInquiryCategoryId(id);
        return inquiryCategory;
    }
}
