package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.anno.Autowired;
import org.paohaijiao.jstark.anno.Service;
import org.paohaijiao.jstark.publish.JApplicationEventPublisher;

@Service
public class UserService {
    public void registerUser(String username) {
        System.out.println("用户注册成功: " + username);
    }
}
