package org.paohaijiao.jstark.service;

import org.paohaijiao.jstark.anno.Service;

@Service
public class UserService {
    public String getUserName(Long userId) {
        return "User-" + userId;
    }
}
