package org.paohaijiao.jstark.context.service;

import lombok.NoArgsConstructor;

public class MyUserServiceImpl implements MyUserService{

    public MyUserServiceImpl() {

    }
    @Override
    public void sayHello(String name) {
        System.out.println("Hello, " + name + "!");
        try {
            Thread.sleep(100); // 模拟耗时操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserInfo(int userId) {
        return "UserInfo-" + userId;
    }
}
