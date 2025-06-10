package com.paohaijiao.javelin;

import com.paohaijiao.javelin.context.JAnnotationConfigApplicationContext;
import com.paohaijiao.javelin.service.UserRule;

public class ScanMain {
    public static void main(String[] args) {
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.paohaijiao.javelin");
        UserRule userService = context.getBean("userRule", UserRule.class);

        // 使用Bean
        System.out.println(userService.findUser(1L));
    }
}
