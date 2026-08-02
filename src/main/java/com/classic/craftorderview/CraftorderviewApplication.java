package com.classic.craftorderview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CraftorderviewApplication {

	public static void main(String[] args) {
		java.util.TimeZone.setDefault(
			java.util.TimeZone.getTimeZone("America/Guayaquil"));
		SpringApplication.run(CraftorderviewApplication.class, args);
	}

}
