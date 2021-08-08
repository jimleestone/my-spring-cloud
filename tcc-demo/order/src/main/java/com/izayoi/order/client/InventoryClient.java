package com.izayoi.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	 * Test decrease boolean.
	 *
	 * @param inventoryDTO the inventory dto
	 * @return the boolean
	 */
	@RequestMapping("/testDecrease")
	Boolean testDecrease(@RequestBody InventoryDTO inventoryDTO);

	/**
	 * 获取商品库存.
	 *
	 * @param productId 商品id
	 * @return InventoryDO integer
	 */
	@RequestMapping("/findByProductId")
	Integer findByProductId(@RequestParam("productId") String productId);

	/**
	 * 模拟库存扣减异常.
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@RequestMapping("/mockWithTryException")
	Boolean mockWithTryException(@RequestBody InventoryDTO inventoryDTO);

	/**
	 * 模拟库存扣减超时.
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@RequestMapping("/mockWithTryTimeout")
	Boolean mockWithTryTimeout(@RequestBody InventoryDTO inventoryDTO);
}
