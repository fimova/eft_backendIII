package com.duoc.bff_atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class BffAtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffAtmApplication.class, args);
	}

}
