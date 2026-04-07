package com.RollNo8.OrderService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);

		//Two things we want to accomplish in this service:
		//1. Create Order object based on Kafka Event and save it to the DB
		//2  Update the inventory to reflect the new value of tickets left.
	}

}
