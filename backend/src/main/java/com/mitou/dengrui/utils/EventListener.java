package com.mitou.dengrui.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

//@Component
public class EventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${server.port}")
    private String port;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Spring 上下文已加载完成");

        String url = "http://localhost:" + port;

        Integer platform = checkPlatform();
        if (platform == 0) {
            // Windows
            openWebOnWindows(url);
        } else if (platform == 3) {
            // Android
            openWebOnAndroid(url);
        } else {
            System.out.println("不支持当前平台！");
        }

    }
    private Integer checkPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.out.println("Windows 操作系统");
            return 0;
        } else if (os.contains("mac")) {
            System.out.println("Mac 操作系统");
            return 1;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            System.out.println("类Unix操作系统");
            return 2;
        } else if (os.contains("android")) {
            System.out.println("安卓设备");
            return 3;
        } else {
            System.out.println("未知操作系统");
            return null;
        }
    }

    private void openWebOnWindows(String url) {
        try {
            Runtime.getRuntime().exec("cmd.exe /c start " + url); // For Windows
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openWebOnAndroid(String url) {
        System.out.println("Java web 程序无法直接打开 Android 系统的浏览器");
    }


}