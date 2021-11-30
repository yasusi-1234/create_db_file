package com.example.create_db_file.from_zero.domain.repository;

import com.example.create_db_file.from_zero.domain.model.DummyUserFirstName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyUserFirstNameRepository extends JpaRepository<DummyUserFirstName, Long> {
}
