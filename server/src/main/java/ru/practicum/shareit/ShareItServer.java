package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.shareit.request.dto.mapper", "ru.practicum.shareit.mapperconfig", "ru.practicum.shareit"})
public class ShareItServer {

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}

}
