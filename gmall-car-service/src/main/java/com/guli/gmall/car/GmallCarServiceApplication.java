package com.guli.gmall.car;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan(basePackages = "com.guli.gmall.car.mapper")
public class GmallCarServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallCarServiceApplication.class, args);
    }

}
