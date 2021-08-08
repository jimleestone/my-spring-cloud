package com.izayoi.order.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.izayoi.common.dto.AccountDTO;
import com.izayoi.common.dto.AccountNestedDTO;

@Component
public class AccountHystrix implements AccountClient {

	@Override
	public Boolean payment(AccountDTO accountDO) {
		System.out.println("执行断路器。。" + accountDO.toString());
		return false;
	}

	@Override
	public Boolean testPayment(AccountDTO accountDO) {
		System.out.println("执行断路器。。" + accountDO.toString());
		return false;
	}

	@Override
	public BigDecimal findByUserId(String userId) {
		System.out.println("执行断路器。。");
		return BigDecimal.ZERO;
	}

	@Override
	public Boolean mockWithTryException(AccountDTO accountDO) {
		return false;
	}

	@Override
	public Boolean mockWithTryTimeout(AccountDTO accountDO) {
		return false;
	}

	@Override
	public Boolean paymentWithNested(AccountNestedDTO nestedDTO) {
		return false;
	}

	@Override
	public Boolean paymentWithNestedException(AccountNestedDTO nestedDTO) {
		return false;
	}

}
