package com.example.PKI.service.interfaces;

import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.domain.TreeNode;
import com.example.PKI.dto.CertificateDTO;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

public interface ICertificateService {
    TreeNode  getAll();
    void buildTree(TreeNode parentNode, CertificateDTO parentCertificate, List<CertificateDTO> certificateDTOS);

    Collection<CertificateDTO> getAllForIssuer(String serialNumber,List<CertificateDTO>certificateDTOS);

    CertificateDTO convertToCetificateDTO(X509Certificate certificate);

    List<X509Certificate> convertToX509(Collection<java.security.cert.Certificate> allCertificates);


    void deleteCertificate(String serialNumber);

    void deleteChildrenCertificates(Certificate parentCertificate);

    public CertificateDTO getCertificateFromKeyStore(String alias);
//    public CertificateDTO invokeCertificate(String alias);

    CertificateDTO invokeCertificate(String alias, String reason);

}
