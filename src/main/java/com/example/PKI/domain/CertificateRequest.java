package com.example.PKI.domain;


import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User subject;

    @Column(name = "issuerSerialNumber")
    private String issuerSerialNumber;

    @Column(name = "date")
    private Date date;

    @Column(name = "status")
    private RequestStatus requestStatus;

    @Column(name = "type")
    private CertificateType certificateType;

    @ManyToMany
    @JoinTable(
            name = "certificate_requests_key_usages",
            joinColumns = @JoinColumn(name = "certificate_request_id"),
            inverseJoinColumns = @JoinColumn(name = "key_usages_id")
    )
    private List<KeyUsages> keyUsages;
}
