package com.izayoi.common.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountDTO {

	private String userId;
	private BigDecimal amount;

}
