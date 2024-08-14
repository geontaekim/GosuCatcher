package com.foo.gosucatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;

@SpringBootApplication(exclude = {
	    ContextStackAutoConfiguration.class,
	    ContextInstanceDataAutoConfiguration.class
	})
public class GosuCatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(GosuCatcherApplication.class, args);
	}
}
