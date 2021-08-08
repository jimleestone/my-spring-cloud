package com.izayoi.account.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.izayoi.account.service.AccountService;
import com.izayoi.common.dto.AccountDTO;
import com.izayoi.common.dto.AccountNestedDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

	private final AccountService accountService;

	@RequestMapping("/payment")
	public Boolean payment(@RequestBody AccountDTO accountDO) {
		return accountService.payment(accountDO);
	}

	@RequestMapping("/testPayment")
	public Boolean testPayment(@RequestBody AccountDTO accountDO) {
		return accountService.testPayment(accountDO);
	}

	@RequestMapping("/mockWithTryException")
	public Boolean mockWithTryException(@RequestBody AccountDTO accountDO) {
		return accountService.mockWithTryException(accountDO);
	}

	@RequestMapping("/mockWithTryTimeout")
	public Boolean mockWithTryTimeout(@RequestBody AccountDTO accountDO) {
		return accountService.mockWithTryTimeout(accountDO);
	}

	@RequestMapping("/paymentWithNested")
	public Boolean paymentWithNested(@RequestBody AccountNestedDTO nestedDTO) {
		return accountService.paymentWithNested(nestedDTO);
	}

	@RequestMapping("/paymentWithNestedException")
	public Boolean paymentWithNestedException(@RequestBody AccountNestedDTO nestedDTO) {
		return accountService.paymentWithNestedException(nestedDTO);
	}

	@RequestMapping("/findByUserId")
	public BigDecimal findByUserId(@RequestParam("userId") String userId) {
		return accountService.findByUserId(userId).getBalance();
	}
}
