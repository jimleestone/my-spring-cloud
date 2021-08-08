package com.izayoi.common.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("order")
public class OrderEntity {

	@TableId(type = IdType.AUTO)
	private Integer id;
	private String number;
	private Integer status;
	private String productId;
	private BigDecimal totalAmount;
	private Integer count;
	private String userId;
	private LocalDateTime createTime;
}
