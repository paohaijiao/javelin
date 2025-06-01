package com.paohaijiao.javelin.service;

import com.paohaijiao.javelin.anno.JComponent;

@JComponent
public class UserRepository {
    public String findUser(Long userId) {
        return "UserData-" + userId;
    }
}