package com.github.paohaijiao.service;

import com.github.paohaijiao.anno.JService;

@JService
public class JUserService {
    public String getUserName(Long userId) {
        return "User-" + userId;
    }
}
