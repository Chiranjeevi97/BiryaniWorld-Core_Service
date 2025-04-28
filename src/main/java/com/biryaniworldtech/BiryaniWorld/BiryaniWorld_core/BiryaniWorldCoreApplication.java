package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BiryaniWorldCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiryaniWorldCoreApplication.class, args);
	}

}
