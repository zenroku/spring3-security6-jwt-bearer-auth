package com.zenroku.spring3.security6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Spring3Security6Application {

	public static void main(String[] args) {
		SpringApplication.run(Spring3Security6Application.class, args);
	}

	// the core configuration in JwtAuthenticationFilter
	// the All of JWT Works on JWTService
	// the implementing spring security of UserDetailService on Application Config

}
