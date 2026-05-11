package com.shopcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		new com.shopcart.config.EnvLoader();
		SpringApplication.run(Main.class, args);
	}
}
