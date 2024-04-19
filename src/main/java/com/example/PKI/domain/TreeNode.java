package com.example.PKI.domain;

import com.example.PKI.dto.CertificateDTO;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TreeNode {
    private CertificateDTO certificate;
    private List<TreeNode> children;

    public TreeNode(CertificateDTO certificate) {
        this.certificate = certificate;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }
}

