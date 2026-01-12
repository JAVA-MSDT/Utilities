package com.javamsdt.masking;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaskingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaskingApplication.class, args);
	}

//	@PostConstruct
//	public void registerCustomConverters() {
//		// Register user's custom converters
//		ConverterFactory.registerCustomConverter(new CustomStringConverter());
//	}
}
