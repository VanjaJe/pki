package com.example.PKI.domain;

import com.example.PKI.domain.enums.CertificateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.cert.X509Certificate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private CertificateType certificateType;

    @Column(name = "date_from")
    private Date dateFrom;

    @Column(name = "date_to")
    private Date dateTo;

    @ManyToOne(cascade = CascadeType.ALL)
    private User subject;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "issuer_serial_number")
    private String issuerSerialNumber;

}
