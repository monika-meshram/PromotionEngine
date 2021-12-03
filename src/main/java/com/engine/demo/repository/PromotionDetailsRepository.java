package com.engine.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;

public interface PromotionDetailsRepository extends MongoRepository<PromotionDetails, String>{
	PromotionDetails findBySku(String sku);
	List<PromotionDetails> findByPromotionType(String promotionType);
}
