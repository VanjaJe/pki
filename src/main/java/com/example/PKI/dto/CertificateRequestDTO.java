package com.example.PKI.dto;


import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsageEnum;
import com.example.PKI.domain.enums.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CertificateRequestDTO {
    private Long id;
    private User subject;
    private String issuerSerialNumber;
    private Date date;
    private RequestStatus requestStatus;
    private CertificateType certificateType;
    private List<KeyUsageEnum> keyUsages;
}
