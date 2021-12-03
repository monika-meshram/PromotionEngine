package com.engine.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.engine.demo.domain.OriginalPrice;

public interface OriginalPricesRepository extends MongoRepository<OriginalPrice, String>{
	OriginalPrice findBySku(String sku);
}
