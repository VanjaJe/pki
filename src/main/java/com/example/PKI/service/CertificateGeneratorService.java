package com.example.PKI.service;

import com.example.PKI.domain.*;
import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsageEnum;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.KeyRepository;
import com.example.PKI.repository.KeyStoreRepository;
import com.example.PKI.service.interfaces.ICertificateGeneratorService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {

    @Autowired
    CertificateRepository certificateRepository;
    private final KeyRepository keyRepository;
    private final KeyStoreRepository keyStoreRepository;

    @Autowired
    public CertificateGeneratorService(KeyRepository keyRepository,
                                       KeyStoreRepository keyStoreRepository) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.keyRepository = keyRepository;
        this.keyStoreRepository = keyStoreRepository;
    }

    @Override
    public X509Certificate generateCertificate(CertificateRequest request) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            Subject subject = generateSubject(request.getSubject(),request.getCertificateType().toString());
            Certificate findAliasCertificate=certificateRepository.findBySerialNumber(request.getIssuerSerialNumber());
            Issuer issuer;
            if (findAliasCertificate==null){
                issuer = generateIssuer("root");
            }else{
                issuer = generateIssuer(findAliasCertificate.getAlias());
            }

//            if (!isExpired(request.getIssuerSerialNumber())) {
//                return null;
//            }
            System.out.printf("nije istekaaaooaoaoa");
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            Date startDate = new Date();
            Date endDate = getEndDate(request.getCertificateType());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(subject.getSerialNumber()),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

//            addKeyUsageExtensions(certGen,request.getKeyUsages());
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            X509Certificate certificate=certConverter.getCertificate(certHolder);
            String alias = generateAlias(subject.getSerialNumber());

            keyStoreRepository.writeCertificate(alias,certificate);
            saveToDatabase(certificate,request,alias);

            return certificate;

        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void addKeyUsageExtensions(X509v3CertificateBuilder certBuilder, List<KeyUsageEnum> keyUsages) throws CertIOException {
//        for (KeyUsageEnum keyUsageEnum : keyUsages) {
//            int keyUsageBits = getKeyUsageBits(keyUsageEnum);
//            certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsageBits));
//        }
//    }
//    private static int getKeyUsageBits(KeyUsageEnum keyUsageEnum) {
//        switch (keyUsageEnum) {
//            case DIGITAL_SIGNATURE:
//                return KeyUsage.digitalSignature;
//            case KEY_ENCIPHERMENT:
//                return KeyUsage.keyEncipherment;
//            case DATA_ENCIPHERMENT:
//                return KeyUsage.dataEncipherment;
//            case KEY_AGREEMENT:
//                return KeyUsage.keyAgreement;
//            case KEY_CERTIFICATE_SIGN:
//                return KeyUsage.keyCertSign;
//            default:
//                throw new IllegalArgumentException();
//        }
//    }

    public void addKeyUsageExtensions(X509v3CertificateBuilder certBuilder, List<String> keyUsages) throws CertIOException {
        for (String keyUsage : keyUsages) {
            int keyUsageBits = getKeyUsageBits(keyUsage);
            certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsageBits));
        }
    }

    private int getKeyUsageBits(String keyUsage) {
        switch (keyUsage) {
            case "DIGITAL_SIGNATURE":
                return KeyUsage.digitalSignature;
            case "KEY_ENCIPHERMENT":
                return KeyUsage.keyEncipherment;
            case "DATA_ENCIPHERMENT":
                return KeyUsage.dataEncipherment;
            case "KEY_AGREEMENT":
                return KeyUsage.keyAgreement;
            case "KEY_CERTIFICATE_SIGN":
                return KeyUsage.keyCertSign;
            default:
                throw new IllegalArgumentException("Invalid key usage: " + keyUsage);
        }
    }



    private void saveToDatabase(X509Certificate x509Certificate, CertificateRequest request, String alias){
        X509Certificate issuerCertificate = (X509Certificate) keyStoreRepository.readCertificate("alias_"+request.getIssuerSerialNumber());

        if(issuerCertificate==null){
            issuerCertificate = (X509Certificate) keyStoreRepository.readCertificate("root");

        }

        Certificate certificate = new Certificate();
        certificate.setCertificateType(request.getCertificateType());
        certificate.setAlias(alias);
        certificate.setSerialNumber(x509Certificate.getSerialNumber().toString());
        certificate.setIssuerSerialNumber(issuerCertificate.getSerialNumber().toString());
        certificate.setRevoked(false);
        certificate.setRevokeReason("");
//        certificate.setKeyUsages(request.getKeyUsages());
        certificate.setSubject(request.getSubject());
        certificateRepository.save(certificate);

    }

    public Subject generateSubject(User user,String type) {
        KeyPair keyPairSubject = generateKeyPair();
        String serialNumber= generateSerialNumber();
        keyRepository.writePrivateKeyToFile(keyPairSubject.getPrivate(),generateAlias(serialNumber));

        //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.SERIALNUMBER, serialNumber);
        builder.addRDN(BCStyle.CN, user.getAccount().getUsername());
        builder.addRDN(BCStyle.DESCRIPTION, type);
        builder.addRDN(BCStyle.SURNAME, user.getLastName());
        builder.addRDN(BCStyle.GIVENNAME, user.getFirstName());
        builder.addRDN(BCStyle.UID, user.getId().toString());

        return new Subject(keyPairSubject.getPublic(), builder.build(),serialNumber);
    }

    private String generateSerialNumber(){
        return String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
    private String generateAlias(String serialNumber){

        return "alias_"+serialNumber;
    }

    public Issuer generateIssuer(String issuerAlias) {
        if(Objects.equals(issuerAlias, "root")){
            X509Certificate certificate= (X509Certificate) keyStoreRepository.readCertificate(issuerAlias);
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.SERIALNUMBER, certificate.getSerialNumber().toString());
            builder.addRDN(BCStyle.CN, "travelbee");
            builder.addRDN(BCStyle.DESCRIPTION, "ROOT");
            builder.addRDN(BCStyle.SURNAME, "root");
            builder.addRDN(BCStyle.GIVENNAME,"root");
            builder.addRDN(BCStyle.UID, "rootid");

            PrivateKey pk= keyRepository.readPrivateKeyFromFile("root");
            return new Issuer(pk, builder.build());

        }else{
            Certificate certificate=certificateRepository.findByAlias(issuerAlias);
            KeyPair kp = generateKeyPair();
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.SERIALNUMBER, certificate.getSerialNumber());
            builder.addRDN(BCStyle.CN, certificate.getSubject().getAccount().getUsername());
            builder.addRDN(BCStyle.DESCRIPTION, certificate.getCertificateType().toString());
            builder.addRDN(BCStyle.SURNAME, certificate.getSubject().getLastName());
            builder.addRDN(BCStyle.GIVENNAME, certificate.getSubject().getFirstName());
            builder.addRDN(BCStyle.UID, certificate.getSubject().getId().toString());
            PrivateKey pk = getIssuerPrivateKey(certificate.getSerialNumber());

            return new Issuer(pk, builder.build());
        }
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getEndDate(CertificateType type) {
        Date endTime;
        if (type == CertificateType.ROOT) {
            endTime = Date.from(LocalDate.now().plusYears(10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (type == CertificateType.INTERMEDIATE) {
            endTime = Date.from(LocalDate.now().plusYears(5).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        else {
            endTime = Date.from(LocalDate.now().plusYears(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return endTime;
    }

    public PrivateKey getIssuerPrivateKey(String serialNumber) {
        String alias = certificateRepository.findBySerialNumber(serialNumber).getAlias();
        return keyRepository.readPrivateKeyFromFile(alias);
    }

    public boolean isExpired(String serialNumber) {
        String alias = generateAlias(serialNumber);

        CertificateService sc = new CertificateService();
        CertificateDTO issuer = sc.getCertificateFromKeyStore(alias);

        Date currentDate = new Date();

        if (issuer.getDateTo().before(currentDate)) {
            return false;
        }
        else {
            CertificateDTO parent = sc.getCertificateFromKeyStore(issuer.getIssuerSerialNumber());
            if (parent == null) {
                return false;
            }
            if (!parent.getSerialNumber().equals(issuer.getSerialNumber())) {
                System.out.printf("usao nekad i ovde");
                isExpired(parent.getSerialNumber());
            }
        }
        return true;
    }

    public boolean isRevoked(String serialNumber) {
        String alias = generateAlias(serialNumber);

        Certificate issuer = certificateRepository.findByAlias(alias);

        if (issuer.isRevoked()) {
            return false;
        }
        else {
            Certificate parent = certificateRepository.findByAlias(alias);
            if (!parent.getSerialNumber().equals(issuer.getSerialNumber())) {
                System.out.printf("usao nekad i ovde");
                isRevoked(parent.getSerialNumber());
            }
        }
        return true;
    }
}
