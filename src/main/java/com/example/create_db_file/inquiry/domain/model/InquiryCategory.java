package com.example.create_db_file.inquiry.domain.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InquiryCategory that = (InquiryCategory) o;
        return inquiryCategoryId != null && Objects.equals(inquiryCategoryId, that.inquiryCategoryId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
