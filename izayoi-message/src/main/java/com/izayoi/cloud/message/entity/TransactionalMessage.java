package com.izayoi.cloud.message.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_transactional_message")
public class TransactionalMessage extends BaseEntity {

	@TableId(type = IdType.AUTO)
	private Long id;
	private Integer currentRetryTimes;
	private Integer maxRetryTimes;
	private String queueName;
	private String exchangeName;
	private String exchangeType;
	private String routingKey;
	private String businessModule;
	private String businessKey;
	private LocalDateTime nextScheduleTime;
	private Integer messageStatus;
	private Long initBackoff;
	private Integer backoffFactor;
}
