package com.amikhaylov.mysimplereminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MySimpleReminderApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySimpleReminderApplication.class, args);
	}

}
