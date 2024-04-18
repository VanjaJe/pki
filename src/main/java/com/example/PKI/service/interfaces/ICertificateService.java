package com.example.PKI.service.interfaces;

import com.example.PKI.domain.Certificate;
import com.example.PKI.domain.TreeNode;

import java.util.Collection;

public interface ICertificateService {
    TreeNode  getAll();
    void buildTree(TreeNode parentNode, Certificate parentCertificate);

    Collection<Certificate> getAllForIssuer(String serialNumber);
}
