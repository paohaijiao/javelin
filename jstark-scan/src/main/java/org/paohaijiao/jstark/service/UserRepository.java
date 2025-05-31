package org.paohaijiao.jstark.service;

import org.paohaijiao.jstark.anno.Component;

@Component
public class UserRepository {
    public String findUser(Long userId) {
        return "UserData-" + userId;
    }
}