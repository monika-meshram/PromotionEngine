package com.engine.demo.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "DefaultPrices")
@Data
public class OriginalPrice {
	private String sku;
	private int price;
}
