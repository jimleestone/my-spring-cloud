package com.izayoi.cloud.message.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.izayoi.cloud.message.common.ExchangeType;
import com.izayoi.cloud.message.common.impl.DefaultDestination;
import com.izayoi.cloud.message.common.impl.DefaultTxMessage;
import com.izayoi.cloud.message.entity.Order;
import com.izayoi.cloud.message.mapper.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MockBusinessService {

	private final OrderMapper orderMapper;
	private final TransactionalMessageService transactionalMessageService;
	private final ObjectMapper objectMapper;

	@Transactional(rollbackFor = Exception.class)
	public void saveOrder() throws Exception {
		String orderId = UUID.randomUUID().toString();
		BigDecimal amount = BigDecimal.valueOf(100L);
		Order order = new Order();
		order.setOrderId(orderId);
		order.setAmount(amount);
		order.setUserId(1L);
		// 保存订单
		orderMapper.insert(order);

		String content = objectMapper.writeValueAsString(order);
		transactionalMessageService.sendTransactionalMessage(
				DefaultDestination.builder().exchangeName("tm.test.exchange").queueName("tm.test.queue")
						.routingKey("tm.test.key").exchangeType(ExchangeType.DIRECT).build(),
				DefaultTxMessage.builder().businessKey(orderId).businessModule("SAVE_ORDER").content(content).build());
		log.info("保存订单:{}成功...", orderId);
	}
}
