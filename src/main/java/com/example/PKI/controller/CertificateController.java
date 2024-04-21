package com.example.PKI.controller;


import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @GetMapping
    public ResponseEntity<TreeNode> getAll () {
        TreeNode certificates=certificateService.getAll();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    @PutMapping("/revokeCertificate")
    public ResponseEntity<String> revokeCertificate(@RequestBody Certificate certificate) {
        Certificate certificateToDelete = certificateService.findBySerialNumber(certificate.getSerialNumber());
        if (certificateToDelete != null) {
            certificateService.revokeCertificate(certificate.getSerialNumber(), certificate.getRevokeReason());
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }
}
