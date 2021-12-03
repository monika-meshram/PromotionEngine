package com.engine.demo.service.impl;

import java.util.Map;

import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;
import com.engine.demo.domain.SKUEnum;
import com.engine.demo.service.OfferProcessingStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualSkuOffer implements OfferProcessingStrategy{

	private SKUEnum sku;
	private Map<SKUEnum, Integer> orderMap;
	private PromotionDetails promotion;
	private OriginalPrice originalPrice;

	@Override
	public int applyPromotion() {
		int price = performPromotionOperation();
		return price;
	}

	private int performPromotionOperation() {
		int offersCount;
		int price = 0;
		if(orderMap.get(sku) >= promotion.getQuantity()) {
			offersCount = orderMap.get(sku) / promotion.getQuantity();
			price += offersCount * promotion.getOfferPrice();
			offersCount = orderMap.get(sku) % promotion.getQuantity();
			price += offersCount * originalPrice.getPrice();
		} else {
			price += orderMap.get(sku) * originalPrice.getPrice();
		}
		return price;
	}
}
