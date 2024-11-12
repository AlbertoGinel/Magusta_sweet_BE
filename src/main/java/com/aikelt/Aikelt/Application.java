package com.aikelt.Aikelt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.aikelt.Aikelt.controller", "com.aikelt.Aikelt.service", "com.aikelt.Aikelt.repository","com.aikelt.Aikelt.config","com.aikelt.Aikelt.security"})
//@ComponentScan(basePackages = {"com.aikelt.Aikelt"})


public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
