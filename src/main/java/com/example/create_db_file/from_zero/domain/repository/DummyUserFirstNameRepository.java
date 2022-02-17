package com.example.create_db_file.from_zero.domain.repository;

import com.example.create_db_file.from_zero.domain.model.DummyUserFirstName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DummyUserFirstNameRepository
        extends JpaRepository<DummyUserFirstName, Long>,
        JpaSpecificationExecutor<DummyUserFirstName> {

    DummyUserFirstName findByUserFirstNameId(Long id);

    @Query("SELECT d FROM DummyUserFirstName d WHERE d.userFirstNameId = :id")
    DummyUserFirstName findByUserFirstNameId2(@Param("id") Long id);

    @Query(nativeQuery = true,
    value = "SELECT " +
                "d.user_first_name_id," +
                "d.first_name," +
                "d.first_name_kana," +
                "d.first_name_roman " +
            "FROM dummy_user_first_name d " +
            "WHERE d.user_first_name_id = :id")
    DummyUserFirstName findByUserFirstNameId3(@Param("id") Long id);

}
