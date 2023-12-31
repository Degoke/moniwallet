package com.degoke.moniwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MoniwalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoniwalletApplication.class, args);
	}

}
