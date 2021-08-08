package com.izayoi.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.izayoi.common.dto.InventoryDTO;

@FeignClient(value = "inventory-service", path = "/inventory-service/inventory")
public interface InventoryClient {

	/**
	 * ���ۼ�.
	 *
	 * @param inventoryDTO ʵ�����
	 * @return true �ɹ�
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
	 * ��ȡ��Ʒ���.
	 *
	 * @param productId ��Ʒid
	 * @return InventoryDO integer
	 */
	@RequestMapping("/findByProductId")
	Integer findByProductId(@RequestParam("productId") String productId);

	/**
	 * ģ����ۼ��쳣.
	 *
	 * @param inventoryDTO ʵ�����
	 * @return true �ɹ�
	 */
	@RequestMapping("/mockWithTryException")
	Boolean mockWithTryException(@RequestBody InventoryDTO inventoryDTO);

	/**
	 * ģ����ۼ���ʱ.
	 *
	 * @param inventoryDTO ʵ�����
	 * @return true �ɹ�
	 */
	@RequestMapping("/mockWithTryTimeout")
	Boolean mockWithTryTimeout(@RequestBody InventoryDTO inventoryDTO);
}
