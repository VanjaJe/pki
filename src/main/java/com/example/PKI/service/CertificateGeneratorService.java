package com.example.PKI.service;

import com.example.PKI.domain.Issuer;
import com.example.PKI.domain.Subject;
import com.example.PKI.service.interfaces.ICertificateGeneratorService;
import org.springframework.stereotype.Service;

import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {
    @Override
    public X509Certificate generateCertificate(Subject subject, Issuer issuer, Date startDate, Date endDate, String serialNumber) {
        return null;
    }
}
