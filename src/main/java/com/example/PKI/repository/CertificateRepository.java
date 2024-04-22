package com.example.PKI.repository;

import com.example.PKI.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate,Long> {
    Collection<Certificate> findAllByIssuerSerialNumber(String serialNumber);
    Certificate findBySerialNumber(String serialNumber);
    Certificate findByAlias(String alias);

    List<Certificate> findBySubjectId(Long userId);

}
