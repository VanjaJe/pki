package com.example.PKI.dto;


import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsageEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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

    private List<KeyUsageEnum> keyUsages;
}
