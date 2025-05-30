package org.paohaijiao.jstark.context.service;

import org.paohaijiao.jstark.anno.Autowired;

public class UserService {
    @Autowired
    private OrderService orderService;

    public void init() {
        System.out.println("UserService initial...");
    }

    public void sayHello() {
        System.out.println("Hello from UserService!");
        System.out.println("i hava OrderService: " + (orderService != null));
    }
}
