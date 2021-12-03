package com.engine.demo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "OrderInfo")
@Data
public class OrderInfo {
	@Id
	private String id;
	private String sku;
	private int quantity;
}

