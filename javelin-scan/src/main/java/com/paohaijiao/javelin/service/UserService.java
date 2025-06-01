package com.paohaijiao.javelin.service;

import com.paohaijiao.javelin.anno.JService;

@JService
public class UserService {
    public String getUserName(Long userId) {
        return "User-" + userId;
    }
}
