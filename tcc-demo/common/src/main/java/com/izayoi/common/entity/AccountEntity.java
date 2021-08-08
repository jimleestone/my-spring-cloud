package com.izayoi.common.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("account")
public class AccountEntity {

	@TableId(type = IdType.AUTO)
	private Integer id;
	private String userId;
	private BigDecimal balance;
	private BigDecimal freezeAmount;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}
