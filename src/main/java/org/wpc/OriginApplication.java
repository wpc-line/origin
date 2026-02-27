package org.wpc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.wpc.mapper")
public class OriginApplication {

    public static void main(String[] args) {
        SpringApplication.run(OriginApplication.class, args);
    }

}
