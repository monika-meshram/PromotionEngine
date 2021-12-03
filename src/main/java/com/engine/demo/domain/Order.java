package com.engine.demo.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "Order")
@Data
public class Order {
	@Id
	private String id;
	List<OrderInfo> orderInfo;
}

