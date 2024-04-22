package com.example.PKI.controller;


import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.mapper.CertificateDTOMapper;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/certificate")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @Autowired
    private CertificateRepository certificateRepository;

    @GetMapping
    public ResponseEntity<TreeNode> getAll () {
        TreeNode certificates=certificateService.getAll();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<CertificateDTO> getCertificate (@PathVariable Long userId) {
        CertificateDTO certificateDTO=certificateService.getCertificateForUser(userId);
        if(certificateDTO==null){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(certificateDTO, HttpStatus.OK);
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

    @DeleteMapping("/delete/{serialNumber}")
    public ResponseEntity<String> deleteResource(@PathVariable String serialNumber) {
        Certificate certificateToDelete = certificateRepository.findBySerialNumber(serialNumber);
        if (certificateToDelete != null) {
            certificateService.deleteCertificate(serialNumber);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }
}
