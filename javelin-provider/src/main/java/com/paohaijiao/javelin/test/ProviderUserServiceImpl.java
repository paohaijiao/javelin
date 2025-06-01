package com.paohaijiao.javelin.test;

public class ProviderUserServiceImpl implements ProviderUserService {
    @Override
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }
}
