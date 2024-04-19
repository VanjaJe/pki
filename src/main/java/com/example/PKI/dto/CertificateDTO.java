package com.example.PKI.dto;


import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CertificateDTO {

    private Long id;

    private CertificateType certificateType;

    private Date dateFrom;

    private Date dateTo;

    private String email;

    private String serialNumber;

    private String issuerSerialNumber;
}
