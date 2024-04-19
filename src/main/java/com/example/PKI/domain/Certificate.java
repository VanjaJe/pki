package com.example.PKI.domain;

import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.domain.enums.KeyUsages;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "alias")
    private String alias;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "issuer_serial_number")
    private String issuerSerialNumber;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    @Column(name = "revoke_reason")
    private String revokeReason;

    @ElementCollection
    private List<KeyUsages> keyUsages;
}
