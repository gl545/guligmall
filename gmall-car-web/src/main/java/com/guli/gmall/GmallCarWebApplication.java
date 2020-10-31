package com.guli.gmall;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
public class GmallCarWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCarWebApplication.class, args);
    }

}
