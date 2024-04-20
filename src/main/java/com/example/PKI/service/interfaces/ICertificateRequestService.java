package com.example.PKI.service.interfaces;


import com.example.PKI.domain.CertificateRequest;

import java.util.Collection;

public interface ICertificateRequestService {

    public CertificateRequest createRequest(CertificateRequest request);

    public Collection<CertificateRequest> getAllRequests();

    public CertificateRequest acceptRequest(CertificateRequest request);
    public CertificateRequest declineRequest(CertificateRequest request);

    CertificateRequest findOne(Long id);

    CertificateRequest updateRequest(CertificateRequest certificateForUpdate, CertificateRequest newCertificateRequest);
}
