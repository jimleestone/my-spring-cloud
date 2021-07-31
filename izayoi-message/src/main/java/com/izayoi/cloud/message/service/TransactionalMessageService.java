package com.izayoi.cloud.message.service;

import com.izayoi.cloud.message.common.Destination;
import com.izayoi.cloud.message.common.TxMessage;

public interface TransactionalMessageService {

	void sendTransactionalMessage(Destination destination, TxMessage message);

}
