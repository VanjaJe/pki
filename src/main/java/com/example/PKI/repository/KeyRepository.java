package com.example.PKI.repository;

import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Repository
public class KeyRepository {

    private String keysPath = "src/main/resources/keys/";

    public void writePrivateKeyToFile(PrivateKey privateKey, String alias) {
        try {
            FileOutputStream fos = new FileOutputStream(keysPath + alias + ".key");
            fos.write(privateKey.getEncoded());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey readPrivateKeyFromFile(String alias) {
        try {
            FileInputStream fis = new FileInputStream(keysPath + alias + ".key");
            byte[] keyBytes = new byte[fis.available()];
            fis.read(keyBytes);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deletePrivateKey(String alias) {
        File privateKeyFile = new File(keysPath + alias + ".key");
        if (privateKeyFile.exists()) {
            privateKeyFile.delete();
        }
    }


}
