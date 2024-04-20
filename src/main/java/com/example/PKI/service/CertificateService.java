package com.example.PKI.service;

import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.service.interfaces.ICertificateService;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        RDN[] rdnsSubject = new X500Name(subjectDN).getRDNs();
        for (RDN rdn : rdnsSubject) {
            if (rdn.getFirst().getType().equals(BCStyle.CN)) {
                certificateDTO.setEmail(rdn.getFirst().getValue().toString());
            }
            if (rdn.getFirst().getType().equals(BCStyle.DESCRIPTION)) {
                certificateDTO.setCertificateType(CertificateType.valueOf(rdn.getFirst().getValue().toString()));
            }
        }
        RDN[] rdnsIssuer = new X500Name(issuerDN).getRDNs();
        for (RDN rdn : rdnsIssuer) {
            if (rdn.getFirst().getType().equals(BCStyle.SERIALNUMBER)) {
                String issuerSerialNumber=rdn.getFirst().getValue().toString();
                certificateDTO.setIssuerSerialNumber(issuerSerialNumber);
                break;
            }
        }
        if(certificateDTO.getIssuerSerialNumber()==null){
            certificateDTO.setIssuerSerialNumber(serialNumber);
            certificateDTO.setCertificateType(CertificateType.ROOT);
        } else if (certificateDTO.getCertificateType()==null) {
            certificateDTO.setCertificateType(CertificateType.END_ENTITY);
        }//samo za sad, nije lepo upisano inace samo prvi if
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
            if(Objects.equals(childCertificate.getIssuerSerialNumber(), childCertificate.getSerialNumber())){
                continue;
            }
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

    @Override
    public CertificateDTO getCertificateFromKeyStore(String alias) {
        KeyStoreRepository ks = new KeyStoreRepository();
        java.security.cert.Certificate certificateSecurity = ks.readCertificate(alias);
        X509Certificate x509Cert = (X509Certificate) certificateSecurity;

        return convertToCetificateDTO(x509Cert);
    }

    @Override
    public CertificateDTO invokeCertificate(String alias, String reason) {
        Certificate certificate = certificateRepository.findByAlias(alias);
        certificate.setRevoked(true);
        certificate.setRevokeReason(reason);
        Collection<Certificate> children = certificateRepository.findAllByIssuerSerialNumber(certificate.getSerialNumber());

        invokeChildren(children);

        return null;
    }

    public void invokeChildren(Collection<Certificate> children) {
        for (Certificate child : children) {
            child.setRevoked(true);
            Collection<Certificate> children2 = certificateRepository.findAllByIssuerSerialNumber(child.getSerialNumber());

            invokeChildren(children2);
        }
    }
}