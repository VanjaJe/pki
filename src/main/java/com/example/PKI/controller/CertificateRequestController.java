package com.example.PKI.controller;
import com.example.PKI.domain.CertificateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/certificateRequest")
public class CertificateRequestController {
    @GetMapping
    public ResponseEntity<Collection<CertificateRequest>> getAll () {
        Collection<CertificateRequest>certificates=new ArrayList<>();
        return new ResponseEntity<Collection<CertificateRequest>>(certificates, HttpStatus.OK);
    }
}
