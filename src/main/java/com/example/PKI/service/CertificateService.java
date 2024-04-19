package com.example.PKI.service;

import com.example.PKI.domain.Subject;
import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.service.interfaces.ICertificateService;
import org.bouncycastle.cert.X509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyStoreRepository keyStoreRepository;

    @Override
    public TreeNode getAll() {
        TreeNode root = new TreeNode(new CertificateDTO());

        Collection<java.security.cert.Certificate> allCertificates = keyStoreRepository.readCertificates();

        List<X509Certificate> x509Certificates = convertToX509(allCertificates);
        List<CertificateDTO>certificateDTOS=new ArrayList<>();


        for (X509Certificate certificate : x509Certificates) {
            certificateDTOS.add(convertToCetificateDTO(certificate));
        }

        for(CertificateDTO certificateDTO:certificateDTOS){
            if (certificateDTO.getSerialNumber().equals(certificateDTO.getIssuerSerialNumber())) {
                root=new TreeNode(certificateDTO);
                buildTree(root, certificateDTO,certificateDTOS);
            }
        }

        return root;
    }

    @Override
    public CertificateDTO convertToCetificateDTO(X509Certificate certificate) {
        String issuerDN = certificate.getIssuerX500Principal().getName();
        String subjectDN = certificate.getSubjectX500Principal().getName();
        CertificateDTO certificateDTO=new CertificateDTO();

        String serialNumber = String.valueOf(certificate.getSerialNumber());
        Date startDate = certificate.getNotBefore();
        Date endDate = certificate.getNotAfter();

        certificateDTO.setDateFrom(startDate);
        certificateDTO.setDateTo(endDate);
        certificateDTO.setSerialNumber(serialNumber);
        String[] dnComponents = subjectDN.split(",");
        for (String dnComponent : dnComponents) {
            String[] keyValue = dnComponent.split("=");
            if (keyValue.length == 2 && keyValue[0].trim().equals("E")) {
                String email=keyValue[1].trim();
                certificateDTO.setEmail(email);
            }
        }
        String[] dnComponentsIssuer = issuerDN.split(",");
        for (String dnComponent : dnComponentsIssuer) {
            String[] keyValue = dnComponent.split("=");
            if (keyValue.length == 2 && keyValue[0].trim().equals("SERIALNUMBER")) {
                String issuerSerialNumber= keyValue[1].trim();
                certificateDTO.setIssuerSerialNumber(issuerSerialNumber);
            }
        }
        return certificateDTO;
    }

    @Override
    public List<X509Certificate> convertToX509(Collection<java.security.cert.Certificate> allCertificates) {
        List<X509Certificate> x509Certificates = new ArrayList<>();
        for (java.security.cert.Certificate certificate : allCertificates) {
            x509Certificates.add((X509Certificate) certificate);
        }

        return  x509Certificates;
    }

    @Override
    public void buildTree(TreeNode parentNode, CertificateDTO parentCertificate,List<CertificateDTO>certificateDTOS) {
        Collection<CertificateDTO> childCertificates = getAllForIssuer(parentCertificate.getSerialNumber(),certificateDTOS);

        for (CertificateDTO childCertificate : childCertificates) {
            TreeNode childNode = new TreeNode(childCertificate);
            parentNode.addChild(childNode);
            buildTree(childNode, childCertificate,certificateDTOS);
        }
    }

    @Override
    public Collection<CertificateDTO> getAllForIssuer(String serialNumber,List<CertificateDTO>certificateDTOS) {
        Collection<CertificateDTO> childCertificates=new ArrayList<>();
        for(CertificateDTO certificateDTO:certificateDTOS){
            if(Objects.equals(certificateDTO.getIssuerSerialNumber(), serialNumber)){
                childCertificates.add(certificateDTO);
            }
        }
        return childCertificates;
    }
}
