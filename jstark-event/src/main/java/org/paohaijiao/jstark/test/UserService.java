package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.anno.JService;

@JService
public class UserService {
    public void registerUser(String username) {
        System.out.println("用户注册成功: " + username);
    }
}
