package com.example.PKI.controller;


import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.enums.CertificateType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/certificate")
public class CertificateController {
    @GetMapping
    public ResponseEntity<Collection<Certificate>> getAll () {
        Collection<Certificate>certificates=new ArrayList<>();
        return new ResponseEntity<Collection<Certificate>>(certificates,HttpStatus.OK);
    }

}
