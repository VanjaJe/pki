package com.example.PKI.service.interfaces;


import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.domain.Issuer;
import com.example.PKI.domain.Subject;

import java.security.cert.X509Certificate;
import java.util.Date;

public interface ICertificateGeneratorService{
    public  X509Certificate generateCertificate(CertificateRequest request);

}
