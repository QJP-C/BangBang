package com.qjp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
//@EnableSwagger2Doc
@EnableCaching
//@ComponentScan("com.qjp.bang")
public class BangApplication {

    public static void main(String[] args) {
        SpringApplication.run(BangApplication.class, args);

    }
}
