package com.engine.demo.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.engine.demo.domain.Order;
import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;
import com.engine.demo.service.OrderCheckoutService;

@RestController
@RequestMapping("/promotionengine")
public class PromotionEngineController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OrderCheckoutService orderCheckoutService;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		logger.info("Test Method");
		return "Working";
	}
	
	@RequestMapping(value = "/processOrder", method = RequestMethod.POST)
	public String processOrder(@RequestBody Order orderDetails) {
		logger.info("Processing Order");
		int finalPrice = orderCheckoutService.processOrder(orderDetails);
		return "Final Price of the Order: " + finalPrice;
	}
	
	@RequestMapping(value = "/saveOriginalPrice", method = RequestMethod.POST)
	public String saveOriginalPrice(@RequestBody OriginalPrice price) {
		logger.info("Processing Order");
		orderCheckoutService.saveOriginalPrice(price);
		return "Working";
	}
	
	@RequestMapping(value = "/savePromotionDetails", method = RequestMethod.POST)
	public String savePromotionDetails(@RequestBody PromotionDetails promotionDetails) {
		logger.info("Processing Order");
		orderCheckoutService.savePromotionDetails(promotionDetails);
		return "Working";
	}
	
}
