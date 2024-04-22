package com.example.PKI.service;

import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsageEnum;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.KeyRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.service.interfaces.ICertificateService;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyStoreRepository keyStoreRepository;

    @Autowired
    KeyRepository keyRepository;

    @Autowired
    private RestTemplate restTemplate;

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
        
        List<KeyUsageEnum>keyUsages=getKeyUsages(certificate);
        certificateDTO.setKeyUsages(keyUsages);

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

    public List<KeyUsageEnum> getKeyUsages(X509Certificate certificate) {
        byte[] extensionValue = certificate.getExtensionValue(Extension.keyUsage.getId());
        List<KeyUsageEnum> keyUsages = new ArrayList<>();
        if (extensionValue != null) {
            try{
                ASN1OctetString octetString = (ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue);
                KeyUsage keyUsageExtension = KeyUsage.getInstance(octetString.getOctets());

                int keyUsageBits = keyUsageExtension.getBytes()[0];

                if ((keyUsageBits & KeyUsage.digitalSignature) != 0) {
                    keyUsages.add(KeyUsageEnum.DIGITAL_SIGNATURE);
                }
                if ((keyUsageBits & KeyUsage.nonRepudiation) != 0) {
                    keyUsages.add(KeyUsageEnum.NON_REPUDIATION);
                }
                if ((keyUsageBits & KeyUsage.keyEncipherment) != 0) {
                    keyUsages.add(KeyUsageEnum.KEY_ENCIPHERMENT);
                }
                if ((keyUsageBits & KeyUsage.dataEncipherment) != 0) {
                    keyUsages.add(KeyUsageEnum.DATA_ENCIPHERMENT);
                }
                if ((keyUsageBits & KeyUsage.keyAgreement) != 0) {
                    keyUsages.add(KeyUsageEnum.KEY_AGREEMENT);
                }
                if ((keyUsageBits & KeyUsage.cRLSign) != 0) {
                    keyUsages.add(KeyUsageEnum.CRL_SIGNING);
                }
                if ((keyUsageBits & KeyUsage.keyCertSign) != 0) {
                    keyUsages.add(KeyUsageEnum.CERTIFICATE_SIGNING);
                }
                if ((keyUsageBits & KeyUsage.encipherOnly) != 0) {
                    keyUsages.add(KeyUsageEnum.ENCRYPT_ONLY);
                }

            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
        return keyUsages;

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
    public void deleteCertificate(String serialNumber) {
        Certificate certificateToDelete = certificateRepository.findBySerialNumber(serialNumber);
        if (certificateToDelete != null) {
            deleteChildrenCertificates(certificateToDelete);

            keyRepository.deletePrivateKey(certificateToDelete.getAlias());
            keyStoreRepository.deleteCertificate(certificateToDelete.getAlias());
            certificateRepository.delete(certificateToDelete);
        }
    }

    @Override
    public void deleteChildrenCertificates(Certificate parentCertificate) {
        Collection<Certificate> childrenCertificates = certificateRepository.findAllByIssuerSerialNumber(parentCertificate.getSerialNumber());
        for (Certificate childCertificate : childrenCertificates) {
            deleteChildrenCertificates(childCertificate);

            keyRepository.deletePrivateKey(childCertificate.getAlias());
            keyStoreRepository.deleteCertificate(childCertificate.getAlias());
            certificateRepository.delete(childCertificate);
        }
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
    public CertificateDTO revokeCertificate(String serialNumber, String reason) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber);
        certificate.setRevoked(true);
        certificate.setRevokeReason(reason);
        Collection<Certificate> children = certificateRepository.findAllByIssuerSerialNumber(certificate.getSerialNumber());

        revokeChildren(children);

        certificateRepository.save(certificate);
        return null;
    }

    @Override
    public Certificate findBySerialNumber(String serialNumber) {
        return certificateRepository.findBySerialNumber(serialNumber);
    }

    public void revokeChildren(Collection<Certificate> children) {
        System.out.println("REVOOOKEEE");
        for (Certificate child : children) {
            child.setRevoked(true);
            child.setRevokeReason("Issuer is revoked.");
            Collection<Certificate> children2 = certificateRepository.findAllByIssuerSerialNumber(child.getSerialNumber());
            certificateRepository.save(child);
            revokeChildren(children2);
        }
    }
    @Override
    public void downloadCertificate(String alias){
        java.security.cert.Certificate cert = keyStoreRepository.readCertificate(alias);
        if(cert != null) {
            try {
                ByteArrayResource resource = new ByteArrayResource(cert.getEncoded());
                sendCertificate(resource);
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public ByteArrayResource sendCertificate(ByteArrayResource byteCertificate){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(byteCertificate, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:8080/api/certificate/download-certificate",
                requestEntity,
                Void.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            // Success
        } else {
            // Handle error
        }
        return null;
    }

    @Override
    public CertificateDTO getCertificateForUser(Long userId) {
        List<Certificate>certificates= certificateRepository.findBySubjectId(userId);
        if(!certificates.isEmpty()){
            Certificate certificate=certificates.get(0);
            return getCertificateFromKeyStore(certificate.getAlias());
        }
        return null;

    }
}

