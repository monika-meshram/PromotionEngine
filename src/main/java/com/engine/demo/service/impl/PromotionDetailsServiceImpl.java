package com.engine.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.engine.demo.repository.PromotionDetailsRepository;
import com.engine.demo.service.PromotionDetailsService;

@Service
public class PromotionDetailsServiceImpl implements PromotionDetailsService {
	
	@Autowired
	private PromotionDetailsRepository promotionDetailsRepository;

}
