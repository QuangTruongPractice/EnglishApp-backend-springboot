package com.tqt.englishApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnglishAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishAppApplication.class, args);
	}

}
