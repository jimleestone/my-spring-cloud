package com.izayoi.order.client;

import org.springframework.stereotype.Component;

import com.izayoi.common.dto.InventoryDTO;

@Component
public class InventoryHystrix implements InventoryClient {

	public Boolean decrease(InventoryDTO inventoryDTO) {
		System.out.println("inventory hystrix.......");
		return false;
	}

	public Boolean testDecrease(InventoryDTO inventoryDTO) {
		System.out.println("inventory hystrix.......");
		return false;
	}

	public Integer findByProductId(String productId) {
		return 0;
	}

	public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
		return false;
	}

	public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
		return false;
	}
}
