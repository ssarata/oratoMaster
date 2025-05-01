package com.bestprogramer.bestprogramer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.bestprogramer.bestprogramer.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class BestprogramerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestprogramerApplication.class, args);
	}
}
