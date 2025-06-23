package com.github.paohaijiao.service;

import com.github.paohaijiao.anno.JRule;

@JRule
public class JUserRule {
    public String findUser(Long userId) {
        return "Rule-" + userId;
    }
}