package com.izayoi.cloud.message.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TxMessageStatus {

	/**
	 * 成功
	 */
	SUCCESS(1),

	/**
	 * 待处理
	 */
	PENDING(0),

	/**
	 * 处理失败
	 */
	FAIL(-1),

	;

	private final Integer status;
}
