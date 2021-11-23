package com.example.create_db_file.domain.repository;

import com.example.create_db_file.domain.model.DummyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyUserRepository extends JpaRepository<DummyUser, Long> {
}
