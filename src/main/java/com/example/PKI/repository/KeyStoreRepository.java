package com.example.PKI.repository;

import com.example.PKI.domain.Issuer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Repository
public class KeyStoreRepository {

    @Autowired
    private KeyRepository keyRepository;
    public KeyStore keyStore;
    private String passwordPath = "src/main/resources/password.txt";
    private String keyStorePath = "src/main/resources/keystore/keystore.jks";


    public void loadKeyStore() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Path.of(passwordPath)));
            keyStore.load(in,readPassword().toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | NoSuchProviderException |
                 CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public Issuer readIssuer(String alias) {
        try {
            loadKeyStore();
            Certificate cert = keyStore.getCertificate(alias);
            PrivateKey privateKey = keyRepository.readPrivateKeyFromFile(alias);

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new Issuer(privateKey,issuerName);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Certificate readCertificate(String alias) {
        try {
            loadKeyStore();
            if(keyStore.isKeyEntry(alias)) {
                Certificate certificate = keyStore.getCertificate(alias);
                return certificate;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String alias, Certificate certificate) {
        try {
            loadKeyStore();
            keyStore.setCertificateEntry(alias, certificate);
            keyStore.store(Files.newOutputStream(Paths.get(keyStorePath)), readPassword().toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String readPassword() {
        try (BufferedReader br = new BufferedReader(new FileReader(passwordPath))) {
            return br.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
