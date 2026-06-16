package com.openSolutions.currencyJournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CurrencyJournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyJournalApplication.class, args);
	}

}
