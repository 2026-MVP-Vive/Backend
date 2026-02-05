package com.seolstudy.seolstudy_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *  Seolstudy-Backend 메인 클래스
 * */

@SpringBootApplication
@EnableJpaAuditing
public class SeolstudyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeolstudyBackendApplication.class, args);
	}

}
