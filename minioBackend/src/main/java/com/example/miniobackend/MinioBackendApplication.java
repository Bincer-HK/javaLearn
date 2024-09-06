package com.example.miniobackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.**.mapper")
public class MinioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinioBackendApplication.class, args);
	}

}
