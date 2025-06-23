package com.github.paohaijiao.service;

import com.github.paohaijiao.anno.JComponent;

@JComponent
public class JUserRepository {
    public String findUser(Long userId) {
        return "UserData-" + userId;
    }
}