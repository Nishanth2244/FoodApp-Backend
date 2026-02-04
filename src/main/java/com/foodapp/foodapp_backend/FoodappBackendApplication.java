package com.foodapp.foodapp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FoodappBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodappBackendApplication.class, args);
	}
	
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**")
//					.allowedOriginPatterns("*") // Allow all origins
//					.allowedMethods("*")
//					.allowedHeaders("*")
//					.exposedHeaders("Authorization", "Refresh-Token")
//					.allowCredentials(true);
//			}
//		};
//	}

}
