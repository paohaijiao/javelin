package com.paohaijiao.javelin.service;

import com.paohaijiao.javelin.anno.JComponent;

@JComponent
public class JUserRepository {
    public String findUser(Long userId) {
        return "UserData-" + userId;
    }
}