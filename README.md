# PromotionEngine

	API Details :
	
	1. /processOrder - To provide Order Information and get the Final Price
		RequestMethod : POST
		RequestJSON :
		{
			"orderInfo": [
				{
					"sku": "A",
					"quantity": 3
				},
				{
					"sku": "B",
					"quantity": 5
				},
				{
					"sku": "C",
					"quantity": 1
				},
				{
					"sku": "D",
					"quantity": 2
				}
			]
		}
		
	2. /saveOriginalPrice - 
		RequestMethod : POST
		RequestJSON :
		{
			"sku": "A",
			"price": 50
		}
	
	3. /savePromotionDetails - 
		{
			"promotionType": "CombinedSkuOffer",
			"sku": [
				"C",
				"D"
			],
			"quantity": "1",
			"offerPrice": "50"
		}