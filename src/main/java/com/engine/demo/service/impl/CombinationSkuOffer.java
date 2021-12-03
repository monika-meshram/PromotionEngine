package com.engine.demo.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;
import com.engine.demo.domain.PromotionTypeEnum;
import com.engine.demo.domain.SKUEnum;
import com.engine.demo.service.OfferProcessingStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombinationSkuOffer implements OfferProcessingStrategy {
	
	private SKUEnum sku;
	private Map<SKUEnum, Integer> orderMap;
	private OriginalPrice originalPrice;
	private Set<SKUEnum> processedSkuSet;
	private List<PromotionDetails> combinedPromotionOffers;
	private Map<String, Integer> allSkuOriginalPriceMap;
	
	@Override
	public int applyPromotion() {
		int price = performPromotionOperations();
		return price;
	}

	private int performPromotionOperations() {
		int price = 0;
		Map<List<SKUEnum>, Integer> offerSkuPriceMap = new HashMap<List<SKUEnum>, Integer>();
		for (PromotionDetails details : combinedPromotionOffers) {
			offerSkuPriceMap.put(details.getSku(), details.getOfferPrice());
		}

		for (List<SKUEnum> combinedOfferSku : offerSkuPriceMap.keySet()) {
			if (combinedOfferSku.contains(sku)) {
				if (orderMap.keySet().containsAll(combinedOfferSku)) {
					Map<SKUEnum, Integer> combinedOfferSkuBoughtQuantityMap = new HashMap<>();
					for (SKUEnum singleSku : combinedOfferSku) {
						combinedOfferSkuBoughtQuantityMap.put(singleSku, orderMap.get(singleSku));
					}

					int minCombinedBoughtQuantity = combinedOfferSkuBoughtQuantityMap.values().stream()
							.min(Comparator.comparing(Integer::valueOf)).orElse(0);

					if (minCombinedBoughtQuantity > 0) {
						// get price for combined offer for minimum quantity of all the skus in combined
						price += minCombinedBoughtQuantity * offerSkuPriceMap.get(combinedOfferSku);

						// get price for remaining individual quantity for remaining sku in combined
						for (SKUEnum ordereredSku : combinedOfferSkuBoughtQuantityMap.keySet()) {
							// combinedOfferSkuBoughtQuantityMap.put(ordereredSku, value)
							int remainingQuantityForRemainingSku = combinedOfferSkuBoughtQuantityMap.get(ordereredSku)
									- minCombinedBoughtQuantity;
							if (remainingQuantityForRemainingSku > 0) {
								Integer orderedSkuOriginalPrice = allSkuOriginalPriceMap.get(ordereredSku.toString());
								price += remainingQuantityForRemainingSku * orderedSkuOriginalPrice;
							}
						}
					}
					processedSkuSet.addAll(combinedOfferSku);
				} else {
					price += orderMap.get(sku) * originalPrice.getPrice();
				}
			}
		}
		return price;
	}
}
