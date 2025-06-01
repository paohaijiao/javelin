package org.paohaijiao.jstark.service;

import org.paohaijiao.jstark.anno.JComponent;

@JComponent
public class UserRepository {
    public String findUser(Long userId) {
        return "UserData-" + userId;
    }
}