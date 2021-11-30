package com.example.create_db_file.inquiry.domain.service;

import com.example.create_db_file.inquiry.domain.model.Inquiry;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class InquiryServiceHelper {

    /**
     * 開始時刻と終了時刻の間を検索するクエリを返却
     * @param begin 開始時刻
     * @param end 終了時刻
     * @return 開始時刻と終了時刻の間を検索するクエリ
     */
    public static Specification<Inquiry> betweenSendDateTime(LocalDateTime begin, LocalDateTime end){
        return begin == null || end == null ? null
                : (root, query, cb) -> cb.between(root.get("sendDateTime"), begin, end);
    }
}
