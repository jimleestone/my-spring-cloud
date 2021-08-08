package com.izayoi.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.izayoi.common.dto.InventoryDTO;

@FeignClient(value = "inventory-service", path = "/inventory-service/inventory")
public interface InventoryClient {

	/**
	 * 库存扣减.
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@RequestMapping("/decrease")
	Boolean decrease(@RequestBody InventoryDTO inventoryDTO);

	/**
	 * 模拟库存扣减异常.
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@RequestMapping("/mockWithTryException")
	Boolean mockWithTryException(@RequestBody InventoryDTO inventoryDTO);
}
