package org.paohaijiao.jstark.service;

import org.paohaijiao.jstark.anno.JService;

@JService
public class UserService {
    public String getUserName(Long userId) {
        return "User-" + userId;
    }
}
