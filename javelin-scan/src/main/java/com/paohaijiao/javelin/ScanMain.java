package com.paohaijiao.javelin;

import com.paohaijiao.javelin.context.JAnnotationConfigApplicationContext;
import com.paohaijiao.javelin.service.UserRepository;
import com.paohaijiao.javelin.service.UserService;

public class ScanMain {
    public static void main(String[] args) {
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("com.paohaijiao.javelin");

        UserService userService = context.getBean("userService", UserService.class);
        UserRepository userRepository = context.getBean("userRepository", UserRepository.class);

        // 使用Bean
        System.out.println(userService.getUserName(1L));
        System.out.println(userRepository.findUser(1L));
    }
}
