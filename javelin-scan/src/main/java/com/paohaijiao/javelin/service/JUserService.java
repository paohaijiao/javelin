package com.paohaijiao.javelin.service;

import com.paohaijiao.javelin.anno.JService;

@JService
public class JUserService {
    public String getUserName(Long userId) {
        return "User-" + userId;
    }
}
