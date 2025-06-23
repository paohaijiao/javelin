package com.github.paohaijiao;

import com.github.paohaijiao.context.JAnnotationConfigApplicationContext;
import com.github.paohaijiao.service.JUserRule;

public class ScanMain {
    public static void main(String[] args) {
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.paohaijiao.javelin");
        JUserRule userService = context.getBean("jUserRule", JUserRule.class);
        System.out.println(userService.findUser(1L));
    }
}
