package com.paohaijiao.javelin.i18n;

import com.paohaijiao.javelin.i18n.util.I18n;

import java.util.Date;
import java.util.Locale;

public class Application {
    public static void main(String[] args) {
        // 简单使用
        System.out.println(I18n.print("welcome"));
        System.out.println(I18n.print("greeting", "Alice"));

        // 切换语言
        I18n.setLocale(Locale.SIMPLIFIED_CHINESE);
        System.out.println(I18n.print("welcome"));
        System.out.println(I18n.print("greeting", "张三"));

        // 带格式化参数
        System.out.println(I18n.print("balance", 12345.67));

        // 指定Locale
        System.out.println(I18n.print("welcome", Locale.FRENCH));

        // 热重载测试（修改资源文件后会自动重新加载）
        while (true) {
            System.out.println(I18n.print("current.time", new Date()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
