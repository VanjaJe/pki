package com.example.PKI.dto;


import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.RequestStatus;
import jakarta.persistence.*;

import java.util.Date;

public class CertificateRequestDTO {
    private Long id;
    private User subject;
    private Certificate certificateIssuer;
    private Date date;
    private RequestStatus requestStatus;
    private CertificateType certificateType;
}
