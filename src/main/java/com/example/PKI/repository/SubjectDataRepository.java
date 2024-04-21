package com.example.PKI.repository;

import com.example.PKI.domain.SubjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectDataRepository extends JpaRepository<SubjectData,Long> {
}
