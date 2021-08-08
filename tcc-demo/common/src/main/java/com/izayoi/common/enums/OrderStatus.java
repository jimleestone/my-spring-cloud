package com.izayoi.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

	NOT_PAY(1, "未支付"),

	PAYING(2, "支付中"),

	PAY_FAIL(3, "支付失败"),

	PAY_SUCCESS(4, "支付成功"),

	;

	private final int code;

	private final String desc;
}
