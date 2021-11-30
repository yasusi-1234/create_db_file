package com.example.create_db_file.inquiry.domain.service;

import com.example.create_db_file.inquiry.domain.model.Inquiry;
import com.example.create_db_file.inquiry.domain.model.InquiryCategory;
import com.example.create_db_file.inquiry.domain.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService{

    private final InquiryRepository inquiryRepository;


    /**
     * 一件分のInquiry情報をデータベースに登録する
     * @param contents 質問・意見などの情報
     * @param inquiryCategoryId カテゴリーの番号
     * @return DBに保存されたInquiry情報
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public Inquiry saveInquiry(String contents, int inquiryCategoryId){
        InquiryCategory inquiryCategory = InquiryCategory.of(inquiryCategoryId);
        Inquiry saveInquiry = Inquiry.of(contents, LocalDateTime.now(), inquiryCategory);

        return inquiryRepository.save(saveInquiry);
    }

    /**
     * 引数で渡された日付の一日前のお問い合わせ情報を返却する
     * @param dateTime 日付情報
     * @return 引数で渡された日付の一日前のお問い合わせ情報を返却する
     */
    @Override
    public List<Inquiry> findAllInquiryForYesterday(LocalDateTime dateTime){
        LocalDateTime begin = LocalDateTime.of(dateTime.toLocalDate().minusDays(1), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(dateTime.toLocalDate().minusDays(1), LocalTime.MAX);

        return inquiryRepository.findAll(
                Specification.where(InquiryServiceHelper.betweenSendDateTime(begin, end))
        );
    }

}
