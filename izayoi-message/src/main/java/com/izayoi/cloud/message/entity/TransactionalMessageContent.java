package com.izayoi.cloud.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("t_transactional_message_content")
public class TransactionalMessageContent {

	@TableId(type = IdType.AUTO)
	private Long id;
	private Long messageId;
	private String content;
}
