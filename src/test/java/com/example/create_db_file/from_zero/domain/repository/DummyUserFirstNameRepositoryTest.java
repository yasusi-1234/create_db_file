package com.example.create_db_file.from_zero.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("DummyUserFirstNameRepositoryのテスト")
class DummyUserFirstNameRepositoryTest {

    @Autowired
    private DummyUserFirstNameRepository firstNameRepository;

    @Test
    @DisplayName("findAllメソッドは100件のデータを取得する")
    void findAllCount(){
        long actualCount = firstNameRepository.count();
        assertEquals(100L, actualCount);
    }
}
