package com.izayoi.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
	 * ģ����ۼ��쳣.
	 *
	 * @param inventoryDTO ʵ�����
	 * @return true �ɹ�
	 */
	@RequestMapping("/mockWithTryException")
	Boolean mockWithTryException(@RequestBody InventoryDTO inventoryDTO);
}
