package com.engine.demo.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.engine.demo.domain.Order;
import com.engine.demo.domain.OrderInfo;
import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;
import com.engine.demo.domain.PromotionTypeEnum;
import com.engine.demo.domain.SKUEnum;
import com.engine.demo.repository.OriginalPricesRepository;
import com.engine.demo.repository.PromotionDetailsRepository;
import com.engine.demo.service.OfferProcessingStrategy;
import com.engine.demo.service.OrderCheckoutService;

@Service
public class OrderCheckoutServiceImpl implements OrderCheckoutService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OriginalPricesRepository originalPriceRepo;
	
	@Autowired
	private PromotionDetailsRepository promotionDetailsRepository;
	
	@Override
	public int processOrder(Order order) {
		List<OrderInfo> orderInfo = order.getOrderInfo();
		Set<SKUEnum> processedSkuSet = new HashSet<SKUEnum>();
		int price = 0;
		Map<SKUEnum,Integer> orderMap = new HashMap<SKUEnum,Integer>();
		for(OrderInfo info : orderInfo) {
			orderMap.put(SKUEnum.valueOf(info.getSku()), info.getQuantity());
		}
		
		
		for(SKUEnum sku : orderMap.keySet()) {
			if(processedSkuSet.contains(sku)) {
				continue;
			}			
			
			PromotionDetails promotion = promotionDetailsRepository.findBySku(sku.toString());
			OriginalPrice originalPrice = originalPriceRepo.findBySku(sku.toString());
			List<OriginalPrice> originalPriceAllSku = originalPriceRepo.findAll();
			Map<String, Integer> allSkuOriginalPriceMap = new HashMap<>();
			for(OriginalPrice op : originalPriceAllSku) {
				allSkuOriginalPriceMap.put(op.getSku(), op.getPrice());
			}
			
			
			//Check for Individual Promotion Offer
			if(promotion.isActive() && promotion.getPromotionType().equals(PromotionTypeEnum.IndividualSkuOffer)) {
				
				price += applyPromotion(new IndividualSkuOffer(sku, orderMap, promotion, originalPrice));
				//int price = 0;
				/*int offersCount;
				if(orderMap.get(sku) >= promotion.getQuantity()) {
					offersCount = orderMap.get(sku) / promotion.getQuantity();
					price += offersCount * promotion.getOfferPrice();
					offersCount = orderMap.get(sku) % promotion.getQuantity();
					price += offersCount * originalPrice.getPrice();
					//System.out.println("Price" + price);
				} else {
					price += orderMap.get(sku) * originalPrice.getPrice();
				}*/
				processedSkuSet.add(sku);
				logger.info("sku : Price" + sku + " : " +price);
			}
			
			//Check for combined Promotion offer
			if(promotion.isActive() && promotion.getPromotionType().equals(PromotionTypeEnum.CombinedSkuOffer)) {
				
				List<PromotionDetails> combinedPromotionOffers = promotionDetailsRepository.findByPromotionType(PromotionTypeEnum.CombinedSkuOffer.toString());
				price += applyPromotion(new CombinationSkuOffer(sku, orderMap, originalPrice, processedSkuSet, combinedPromotionOffers, allSkuOriginalPriceMap));
				
				
				//List<PromotionDetails> combinedPromotionOffers = promotionDetailsRepository.findByPromotionType(PromotionTypeEnum.CombinedSkuOffer.toString());
				/*Map<List<SKUEnum>, Integer> offerSkuPriceMap = new HashMap<List<SKUEnum>, Integer>();
				for(PromotionDetails details : combinedPromotionOffers) {
					offerSkuPriceMap.put(details.getSku(), details.getOfferPrice());
				}
				
				for(List<SKUEnum> combinedOfferSku : offerSkuPriceMap.keySet()) {
					if(combinedOfferSku.contains(sku)) {
						logger.info("combinedOfferSku contains : " + sku);
						if(orderMap.keySet().containsAll(combinedOfferSku)) {
							Map<SKUEnum, Integer> combinedOfferSkuBoughtQuantityMap = new HashMap<>();
							for(SKUEnum singleSku : combinedOfferSku) {
								combinedOfferSkuBoughtQuantityMap.put(singleSku, orderMap.get(singleSku));
							}
							
							int minCombinedBoughtQuantity = combinedOfferSkuBoughtQuantityMap.values().stream().min(Comparator.comparing(Integer::valueOf)).orElse(0);
							
							if(minCombinedBoughtQuantity > 0) {
								//get price for combined offer for minimum quantity of all the skus in combined offer
								price+=minCombinedBoughtQuantity*offerSkuPriceMap.get(combinedOfferSku);
								
								// get price for remaining individual quantity for remaining sku in combined offer
								for(SKUEnum ordereredSku : combinedOfferSkuBoughtQuantityMap.keySet()) {
									//combinedOfferSkuBoughtQuantityMap.put(ordereredSku, value)
									
									int remainingQuantityForRemainingSku = combinedOfferSkuBoughtQuantityMap.get(ordereredSku)-minCombinedBoughtQuantity;
									if(remainingQuantityForRemainingSku > 0) {
										OriginalPrice orderedSkuOriginalPrice = originalPriceRepo.findBySku(ordereredSku.toString());
										price += remainingQuantityForRemainingSku * orderedSkuOriginalPrice.getPrice();
									}
									
								}
							}
							
							logger.info(orderMap + "combinedOfferSku contains list: " + combinedOfferSku);
							//price+=offerSkuPriceMap.get(combinedOfferSku);
							processedSkuSet.addAll(combinedOfferSku);
						} else {
							price += orderMap.get(sku) * originalPrice.getPrice();
						}
						logger.info("sku : Price" + sku + " : " +price);
					}
				}*/
			}
		}
		
		logger.info("Final Price : " + price);
		return price;
	}
	
	public int applyPromotion(OfferProcessingStrategy offerProcessingStrategy){
		return offerProcessingStrategy.applyPromotion();
		
	}
	
	
	@Override
	public void saveOriginalPrice(OriginalPrice price) {
		OriginalPrice originalPriceList = originalPriceRepo.save(price);
		logger.info("OriginalPrices :" + originalPriceList);

	}

	@Override
	public void savePromotionDetails(PromotionDetails promotionDetails) {
		PromotionDetails promoDetails = promotionDetailsRepository.save(promotionDetails);
		logger.info("PromotionDetails :" + promoDetails);
		
	}
	

}
