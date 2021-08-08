package com.izayoi.common.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountNestedDTO {

	private String userId;
	private BigDecimal amount;
	private String productId;
	private Integer count;
}
