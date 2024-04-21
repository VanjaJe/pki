package com.example.PKI.service;

import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.CertificateRequestRepository;
import com.example.PKI.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    CertificateRequestRepository certificateRequestRepository;

    @Autowired
    CertificateGeneratorService certificateGeneratorService;

    @Autowired
    CertificateRepository certificateRepository;

    @Override
    public CertificateRequest createRequest(CertificateRequest request) {
        return certificateRequestRepository.save(request);
    }

    @Override
    public Collection<CertificateRequest> getAllRequests() {
        return certificateRequestRepository.findAll();
    }

    @Override
    public CertificateRequest acceptRequest(CertificateRequest request) {
        return null;
    }

    @Override
    public CertificateRequest declineRequest(CertificateRequest request) {
        return null;
    }

    @Override
    public CertificateRequest findOne(Long id) {
        return certificateRequestRepository.findById(id).orElse(null);
    }

    @Override
    public CertificateRequest updateRequest(CertificateRequest certificateForUpdate, CertificateRequest newCertificateRequest) {
        Certificate issuer = certificateRepository.findBySerialNumber(newCertificateRequest.getIssuerSerialNumber());

        if (issuer != null && certificateGeneratorService.isRevoked(issuer.getSerialNumber())) {
            return null;
        }

        certificateForUpdate.setSubject(newCertificateRequest.getSubject());
        certificateForUpdate.setIssuerSerialNumber(newCertificateRequest.getIssuerSerialNumber());
        certificateForUpdate.setDate(newCertificateRequest.getDate());
        certificateForUpdate.setRequestStatus(newCertificateRequest.getRequestStatus());
        certificateForUpdate.setCertificateType(newCertificateRequest.getCertificateType());
        certificateForUpdate.setKeyUsages(newCertificateRequest.getKeyUsages());

        CertificateRequest request=certificateRequestRepository.save(certificateForUpdate);

        certificateGeneratorService.generateCertificate(request);

        return request;
    }
}
