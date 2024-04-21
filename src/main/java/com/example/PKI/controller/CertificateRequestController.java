package com.example.PKI.controller;
import com.example.PKI.domain.CertificateRequest;
import com.example.PKI.dto.CertificateDTO;
import com.example.PKI.dto.CertificateRequestDTO;
import com.example.PKI.exception.CertificateEndEntityException;
import com.example.PKI.exception.CertificateRevokedException;
import com.example.PKI.exception.ExtensionsCheckFailedException;
import com.example.PKI.mapper.CertificateRequestDTOMapper;
import com.example.PKI.service.CertificateRequestService;
import jakarta.validation.Valid;
import org.bouncycastle.oer.its.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/certificateRequest")
public class CertificateRequestController {

    @Autowired
    CertificateRequestService certificateRequestService;
    @GetMapping
    public ResponseEntity<Collection<CertificateRequestDTO>> getAll () {
        Collection<CertificateRequest>certificates=certificateRequestService.getAllRequests();
        Collection<CertificateRequestDTO> requestDTOS = certificates.stream()
                .map(CertificateRequestDTOMapper::fromCertificateRequestToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<Collection<CertificateRequestDTO>>(requestDTOS, HttpStatus.OK);
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateRequestDTO> getCertificateRequest(@PathVariable("id") Long id) {
        CertificateRequest certificateRequest = certificateRequestService.findOne(id);

        if (certificateRequest == null) {
            return new ResponseEntity<CertificateRequestDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CertificateRequestDTO>(CertificateRequestDTOMapper.fromCertificateRequestToDTO(certificateRequest), HttpStatus.OK);
    }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateRequestDTO> createCertificateRequest(@RequestBody CertificateRequestDTO certificateRequest) throws Exception {
        CertificateRequest newCertificateRequest= CertificateRequestDTOMapper.fromDTOtoCertificateRequest(certificateRequest);
        CertificateRequest savedRequest = certificateRequestService.createRequest(newCertificateRequest);
        return new ResponseEntity<CertificateRequestDTO>(CertificateRequestDTOMapper.fromCertificateRequestToDTO(savedRequest), HttpStatus.CREATED);
    }
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateRequestDTO> updateCertificateRequest(@RequestBody CertificateRequestDTO certificateRequest) throws Exception {
        CertificateRequest certificateForUpdate = certificateRequestService.findOne(certificateRequest.getId());
        if(certificateForUpdate==null){
            return new ResponseEntity<CertificateRequestDTO>(HttpStatus.BAD_REQUEST);
        }else {
            try{
                CertificateRequest newCertificateRequest = CertificateRequestDTOMapper.fromDTOtoCertificateRequest(certificateRequest);
                CertificateRequest savedRequest = certificateRequestService.updateRequest(certificateForUpdate, newCertificateRequest);
                return new ResponseEntity<CertificateRequestDTO>(CertificateRequestDTOMapper.fromCertificateRequestToDTO(savedRequest), HttpStatus.CREATED);
            }catch (ExtensionsCheckFailedException e) {
                return new ResponseEntity<CertificateRequestDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
            }catch (CertificateRevokedException e) {
                return new ResponseEntity<CertificateRequestDTO>(HttpStatus.NOT_FOUND);
            }catch (CertificateEndEntityException e) {
                return new ResponseEntity<CertificateRequestDTO>(HttpStatus.FORBIDDEN);
            }

        }

    }
}
