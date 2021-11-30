package com.example.create_db_file.from_zero.domain.repository;

import com.example.create_db_file.from_zero.domain.model.DummyUserLastName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyUserLastNameRepository extends JpaRepository<DummyUserLastName, Long> {
}
