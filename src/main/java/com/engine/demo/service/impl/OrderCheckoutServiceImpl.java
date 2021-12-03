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
				processedSkuSet.add(sku);
				logger.info("sku : Price" + sku + " : " +price);
			}
			
			//Check for combined Promotion offer
			if(promotion.isActive() && promotion.getPromotionType().equals(PromotionTypeEnum.CombinedSkuOffer)) {
				
				List<PromotionDetails> combinedPromotionOffers = promotionDetailsRepository.findByPromotionType(PromotionTypeEnum.CombinedSkuOffer.toString());
				price += applyPromotion(new CombinationSkuOffer(sku, orderMap, originalPrice, processedSkuSet, combinedPromotionOffers, allSkuOriginalPriceMap));
			}
		}
		
		logger.info("Final Price of the Order : " + price);
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
