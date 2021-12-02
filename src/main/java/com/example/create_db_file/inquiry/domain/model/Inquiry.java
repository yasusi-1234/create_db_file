package com.example.create_db_file.inquiry.domain.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Inquiry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private LocalDateTime sendDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inquiry_category_id")
    private InquiryCategory inquiryCategory;

    public static Inquiry of(String contents, LocalDateTime sendDateTime, InquiryCategory inquiryCategory){
        Inquiry inquiry = new Inquiry();
        inquiry.setContents(contents);
        inquiry.setSendDateTime(sendDateTime);
        inquiry.setInquiryCategory(inquiryCategory);
        return inquiry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Inquiry inquiry = (Inquiry) o;
        return inquiryId != null && Objects.equals(inquiryId, inquiry.inquiryId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
