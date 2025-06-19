package com.paohaijiao.javelin.service;

import com.paohaijiao.javelin.anno.JRule;

@JRule
public class JUserRule {
    public String findUser(Long userId) {
        return "Rule-" + userId;
    }
}