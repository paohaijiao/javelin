package org.paohaijiao.jstark;

import org.paohaijiao.jstark.context.JAnnotationConfigApplicationContext;
import org.paohaijiao.jstark.service.UserRepository;
import org.paohaijiao.jstark.service.UserService;

public class ScanMain {
    public static void main(String[] args) {
        JAnnotationConfigApplicationContext context =
                new JAnnotationConfigApplicationContext("org.paohaijiao.jstark");

        UserService userService = context.getBean("userService", UserService.class);
        UserRepository userRepository = context.getBean("userRepository", UserRepository.class);

        // 使用Bean
        System.out.println(userService.getUserName(1L));
        System.out.println(userRepository.findUser(1L));
    }
}
