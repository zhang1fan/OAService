package com.xjj.oaoa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xjj.oaoa.dao")
public class OaoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaoaApplication.class, args);
    }



}
