package com.izayoi.cloud.message.common;

public interface Destination {

	ExchangeType exchangeType();

	String queueName();

	String exchangeName();

	String routingKey();
}
