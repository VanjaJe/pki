package com.example.PKI.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TreeNode {
    private Certificate certificate;
    private List<TreeNode> children;

    public TreeNode(Certificate certificate) {
        this.certificate = certificate;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }
}

