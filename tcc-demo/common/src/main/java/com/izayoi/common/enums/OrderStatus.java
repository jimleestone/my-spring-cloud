package com.izayoi.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

	NOT_PAY(1, "δ֧��"),

	PAYING(2, "֧����"),

	PAY_FAIL(3, "֧��ʧ��"),

	PAY_SUCCESS(4, "֧���ɹ�"),

	;

	private final int code;

	private final String desc;
}
