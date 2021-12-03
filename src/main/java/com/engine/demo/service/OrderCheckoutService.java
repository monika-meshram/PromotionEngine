package com.engine.demo.service;

import java.util.List;

import com.engine.demo.domain.Order;
import com.engine.demo.domain.OriginalPrice;
import com.engine.demo.domain.PromotionDetails;

public interface OrderCheckoutService {
	public int processOrder(Order orderDetails);

	public void saveOriginalPrice(OriginalPrice price);

	public void savePromotionDetails(PromotionDetails promotionDetails);
}
