package com.example.PKI.service;

import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsageEnum;
import com.example.PKI.exception.CertificateEndEntityException;
import com.example.PKI.exception.CertificateRevokedException;
import com.example.PKI.exception.ExtensionsCheckFailedException;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.CertificateRequestRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.repository.SubjectDataRepository;
import com.example.PKI.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    CertificateRequestRepository certificateRequestRepository;

    @Autowired
    CertificateGeneratorService certificateGeneratorService;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    SubjectDataRepository subjectDataRepository;

    @Autowired
    KeyStoreRepository keyStoreRepository;

    @Autowired
    CertificateService certificateService;

    @Override
    public CertificateRequest createRequest(CertificateRequest request) {
        subjectDataRepository.save(request.getSubject());
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

        if(issuer!=null && issuer.getCertificateType()== CertificateType.END_ENTITY){
            throw  new CertificateEndEntityException("Certificate issuer is revoked.");
        }

        if (issuer != null && certificateGeneratorService.isRevoked(issuer.getSerialNumber())) {
            throw new CertificateRevokedException("Certificate issuer is revoked.");
        }

        if (!checkExtensions(certificateForUpdate,issuer)){
            throw new ExtensionsCheckFailedException("Extensions check failed.");
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

    private boolean checkExtensions(CertificateRequest certificateForUpdate, Certificate issuer) {
        if(issuer==null){   //root
            return true;
        }
        java.security.cert.Certificate certificate=keyStoreRepository.readCertificate(issuer.getAlias());
        List<KeyUsageEnum> issuerKeyUsages=certificateService.getKeyUsages((X509Certificate) certificate);
        return issuerKeyUsages.size() >= certificateForUpdate.getKeyUsages().size();
    }
}
