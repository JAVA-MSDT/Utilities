package com.javamsdt.masking;

import com.javamsdt.masking.maskconverter.CustomStringConverter;
import com.javamsdt.masking.maskme.api.converter.ConverterFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaskingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaskingApplication.class, args);
	}

	@PostConstruct
	public void registerCustomConverters() {
		// Register user's custom converters
		ConverterFactory.registerCustomConverter(new CustomStringConverter());
	}
}
