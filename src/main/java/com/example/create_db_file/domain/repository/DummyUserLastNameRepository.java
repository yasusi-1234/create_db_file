package com.example.create_db_file.domain.repository;

import com.example.create_db_file.domain.model.DummyUserLastName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyUserLastNameRepository extends JpaRepository<DummyUserLastName, Long> {
}
