package com.example.PKI.exception;

public class CertificateRevokedException extends RuntimeException{
    public CertificateRevokedException(String message) {
        super(message);
    }
}
