package com.example.PKI.service;

import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.TreeNode;
import com.example.PKI.domain.User;
import com.example.PKI.domain.enums.CertificateType;
import com.example.PKI.repository.CertificateRepository;
import com.example.PKI.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Override
    public TreeNode getAll() {
        TreeNode root = new TreeNode(new Certificate());
        
        Collection<Certificate> allCertificates = certificateRepository.findAll();

        for (Certificate certificate : allCertificates) {
            if (certificate.getCertificateType() == CertificateType.ROOT) {
                root=new TreeNode(certificate);
                buildTree(root, certificate);
            }
        }

        return root;
    }

    @Override
    public void buildTree(TreeNode parentNode, Certificate parentCertificate) {
        Collection<Certificate> childCertificates = getAllForIssuer(parentCertificate.getSerialNumber());

        for (Certificate childCertificate : childCertificates) {
            TreeNode childNode = new TreeNode(childCertificate);
            parentNode.addChild(childNode);
            buildTree(childNode, childCertificate);
        }
    }

    @Override
    public Collection<Certificate> getAllForIssuer(String serialNumber) {
        return certificateRepository.findAllByIssuerSerialNumber(serialNumber);
    }
}
