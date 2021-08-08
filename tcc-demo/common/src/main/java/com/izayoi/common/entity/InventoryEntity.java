package com.izayoi.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("inventory")
public class InventoryEntity {

	@TableId(type = IdType.AUTO)
	private Integer id;
	private String productId;
	private Integer totalInventory;
	private Integer lockInventory;
}
