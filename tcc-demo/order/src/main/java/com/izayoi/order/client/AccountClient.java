package com.izayoi.order.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.izayoi.common.dto.AccountDTO;
import com.izayoi.common.dto.AccountNestedDTO;

@FeignClient(value = "account-service", path = "/account-service/account")
public interface AccountClient {

	/**
	 * 用户账户付款.
	 *
	 * @param accountDO 实体类
	 * @return true 成功
	 */
	@RequestMapping("/payment")
	Boolean payment(@RequestBody AccountDTO accountDO);

	/**
	 * Test payment boolean.
	 *
	 * @param accountDO the account do
	 * @return the boolean
	 */
	@RequestMapping("/testPayment")
	Boolean testPayment(@RequestBody AccountDTO accountDO);

	/**
	 * 获取用户账户信息.
	 *
	 * @param userId 用户id
	 * @return AccountDO big decimal
	 */
	@RequestMapping("/findByUserId")
	BigDecimal findByUserId(@RequestParam("userId") String userId);

	/**
	 * Mock with try exception boolean.
	 *
	 * @param accountDO the account do
	 * @return the boolean
	 */
	@RequestMapping("/mockWithTryException")
	Boolean mockWithTryException(@RequestBody AccountDTO accountDO);

	/**
	 * Mock with try timeout boolean.
	 *
	 * @param accountDO the account do
	 * @return the boolean
	 */
	@RequestMapping("/mockWithTryTimeout")
	Boolean mockWithTryTimeout(@RequestBody AccountDTO accountDO);

	/**
	 * Payment with nested boolean.
	 *
	 * @param nestedDTO the nested dto
	 * @return the boolean
	 */
	@RequestMapping("/paymentWithNested")
	Boolean paymentWithNested(@RequestBody AccountNestedDTO nestedDTO);

	/**
	 * Payment with nested exception boolean.
	 *
	 * @param nestedDTO the nested dto
	 * @return the boolean
	 */
	@RequestMapping("/paymentWithNestedException")
	Boolean paymentWithNestedException(@RequestBody AccountNestedDTO nestedDTO);
}
