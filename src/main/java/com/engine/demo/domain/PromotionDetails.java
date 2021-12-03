package com.engine.demo.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "PromotionDetails")
@Data
public class PromotionDetails {

	@Id
	private String id;
	private PromotionTypeEnum promotionType;
	private List<SKUEnum> sku;
	private int quantity;
	private int offerPrice;
	private boolean isActive = true;
}
