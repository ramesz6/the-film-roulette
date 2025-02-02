package com.GyT.The_Film_Roulette;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.GyT.The_Film_Roulette.models") 
public class TheFilmRouletteApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheFilmRouletteApplication.class, args);
	}

}
