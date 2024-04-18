package com.example.PKI.mapper;


import com.example.PKI.domain.Certificate;
import com.example.PKI.dto.CertificateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateDTOMapper {
    private static ModelMapper modelMapper;

    @Autowired
    public CertificateDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public static Certificate fromDTOtoCertificate(CertificateDTO dto) {
        return modelMapper.map(dto, Certificate.class);
    }

    public static CertificateDTO fromCertificatetoDTO(Certificate dto) {
        return modelMapper.map(dto, CertificateDTO.class);
    }
}
