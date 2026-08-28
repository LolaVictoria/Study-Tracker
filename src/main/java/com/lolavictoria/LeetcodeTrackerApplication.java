package com.lolavictoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeetcodeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeetcodeTrackerApplication.class, args);
	}

}
