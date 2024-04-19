package com.example.PKI.service;

import com.example.PKI.domain.*;
import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.repository.KeyRepository;
import com.example.PKI.service.interfaces.ICertificateGeneratorService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    KeyRepository keyRepository;

    @Override
    public X509Certificate generateCertificate(CertificateRequest request) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            Subject subject = generateSubject(request.getSubject());
            Issuer issuer = generateIssuer(request.getCertificateIssuer());

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuer.getPrivateKey());

            Date startDate = new Date();
            Date endDate = getEndDate(request.getCertificateType());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuer.getX500Name(),
                    new BigInteger(generateAlias()),
                    startDate,
                    endDate,
                    subject.getX500Name(),
                    subject.getPublicKey());

            //Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);

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

    public Subject generateSubject(User user) {
        KeyPair keyPairSubject = generateKeyPair();

        //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
//        builder.addRDN(BCStyle.SERIALNUMBER, generateAlias());
        builder.addRDN(BCStyle.CN, user.getAccount().getUsername());
        builder.addRDN(BCStyle.SURNAME, user.getLastName());
        builder.addRDN(BCStyle.GIVENNAME, user.getFirstName());
        builder.addRDN(BCStyle.UID, user.getId().toString());

        return new Subject(keyPairSubject.getPublic(), builder.build());
    }

    private String generateAlias(){
        return String.valueOf(new Random().nextLong());
    }

    public Issuer generateIssuer(Certificate certificate) {
        KeyPair kp = generateKeyPair();
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.SERIALNUMBER, certificate.getSerialNumber());
        builder.addRDN(BCStyle.UNIQUE_IDENTIFIER, certificate.getAlias());
        builder.addRDN(BCStyle.NAME, String.valueOf(certificate.getCertificateType()));

        PrivateKey pk = getIssuerPrivateKey(certificate.getSerialNumber());

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new Issuer(pk, builder.build());
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
}
