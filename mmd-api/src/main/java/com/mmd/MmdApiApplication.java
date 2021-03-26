package com.mmd;

import com.mmd.swagger.annotation.EnableDSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDSwagger2
public class MmdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmdApiApplication.class, args);
    }

}
