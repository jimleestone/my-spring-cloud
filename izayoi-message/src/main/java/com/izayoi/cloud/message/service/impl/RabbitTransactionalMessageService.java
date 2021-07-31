package com.izayoi.cloud.message.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.izayoi.cloud.message.common.Destination;
import com.izayoi.cloud.message.common.ExchangeType;
import com.izayoi.cloud.message.common.TxMessage;
import com.izayoi.cloud.message.entity.TransactionalMessage;
import com.izayoi.cloud.message.service.TransactionalMessageManagementService;
import com.izayoi.cloud.message.service.TransactionalMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitTransactionalMessageService implements TransactionalMessageService {

	private final AmqpAdmin amqpAdmin;
	private final TransactionalMessageManagementService managementService;

	private static final ConcurrentMap<String, Boolean> QUEUE_ALREADY_DECLARE = new ConcurrentHashMap<>();

	@Override
	public void sendTransactionalMessage(Destination destination, TxMessage message) {
		String queueName = destination.queueName();
		String exchangeName = destination.exchangeName();
		String routingKey = destination.routingKey();
		ExchangeType exchangeType = destination.exchangeType();
		// 原子性的预声明
		QUEUE_ALREADY_DECLARE.computeIfAbsent(queueName, k -> {
			Queue queue = new Queue(queueName);
			amqpAdmin.declareQueue(queue);
			Exchange exchange = new CustomExchange(exchangeName, exchangeType.getType());
			amqpAdmin.declareExchange(exchange);
			Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
			amqpAdmin.declareBinding(binding);
			return true;
		});
		TransactionalMessage record = new TransactionalMessage();
		record.setQueueName(queueName);
		record.setExchangeName(exchangeName);
		record.setExchangeType(exchangeType.getType());
		record.setRoutingKey(routingKey);
		record.setBusinessModule(message.businessModule());
		record.setBusinessKey(message.businessKey());
		String content = message.content();

		// 保存事务消息记录
		managementService.saveTransactionalMessageRecord(record, content);
		// 注册事务同步器
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				managementService.sendMessageSync(record, content);
			}
		});
	}
}
