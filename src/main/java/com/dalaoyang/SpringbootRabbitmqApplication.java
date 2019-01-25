package com.dalaoyang;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
@EnableTransactionManagement
public class SpringbootRabbitmqApplication {

	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper=new ObjectMapper();
		return objectMapper;
	}

//	不使用spring boot内嵌tomcat启动方式
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringbootRabbitmqApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRabbitmqApplication.class, args);
	}
}
