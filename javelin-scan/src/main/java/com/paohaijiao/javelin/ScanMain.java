package com.paohaijiao.javelin;

import com.paohaijiao.javelin.context.JAnnotationConfigApplicationContext;
import com.paohaijiao.javelin.service.JUserRule;

public class ScanMain {
    public static void main(String[] args) {
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.paohaijiao.javelin");
        JUserRule userService = context.getBean("jUserRule", JUserRule.class);
        System.out.println(userService.findUser(1L));
    }
}
