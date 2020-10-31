package com.guli.gmall;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration
@MapperScan(basePackages = "com.guli.gmall.payment.mapper")
public class GamllPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamllPaymentApplication.class, args);
    }

}
