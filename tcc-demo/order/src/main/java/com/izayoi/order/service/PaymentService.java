package com.izayoi.order.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.izayoi.common.dto.AccountDTO;
import com.izayoi.common.dto.AccountNestedDTO;
import com.izayoi.common.dto.InventoryDTO;
import com.izayoi.common.entity.OrderEntity;
import com.izayoi.common.enums.OrderStatus;
import com.izayoi.common.mapper.OrderMapper;
import com.izayoi.order.client.AccountClient;
import com.izayoi.order.client.InventoryClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

	private final OrderMapper orderMapper;

	private final AccountClient accountClient;

	private final InventoryClient inventoryClient;

	public void makePayment(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
//        //检查数据
//        final BigDecimal accountInfo = accountClient.findByUserId(order.getUserId());
//        final Integer inventoryInfo = inventoryClient.findByProductId(order.getProductId());
//        if (accountInfo.compareTo(order.getTotalAmount()) < 0) {
//            throw new HmilyRuntimeException("余额不足！");
//        }
//        if (inventoryInfo < order.getCount()) {
//            throw new HmilyRuntimeException("库存不足！");
//        }
		accountClient.payment(buildAccountDTO(order));
		inventoryClient.decrease(buildInventoryDTO(order));
	}

	public void testMakePayment(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
		// 扣除用户余额
		accountClient.testPayment(buildAccountDTO(order));
		// 进入扣减库存操作
		inventoryClient.testDecrease(buildInventoryDTO(order));
	}

	public String mockPaymentInventoryWithTryException(OrderEntity order) {
		log.debug("===========执行springcloud  mockPaymentInventoryWithTryException 扣减资金接口==========");
		updateOrderStatus(order, OrderStatus.PAYING);
		// 扣除用户余额
		accountClient.payment(buildAccountDTO(order));
		inventoryClient.mockWithTryException(buildInventoryDTO(order));
		return "success";
	}

	public String mockPaymentAccountWithTryException(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
		accountClient.mockWithTryException(buildAccountDTO(order));
		return "success";
	}

	public String mockPaymentInventoryWithTryTimeout(OrderEntity order) {
		log.debug("===========执行springcloud  mockPaymentInventoryWithTryTimeout 扣减资金接口==========");
		updateOrderStatus(order, OrderStatus.PAYING);
		accountClient.payment(buildAccountDTO(order));
		inventoryClient.mockWithTryTimeout(buildInventoryDTO(order));
		return "success";
	}

	public String mockPaymentAccountWithTryTimeout(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
		accountClient.mockWithTryTimeout(buildAccountDTO(order));
		return "success";
	}

	public String makePaymentWithNested(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
		final BigDecimal balance = accountClient.findByUserId(order.getUserId());
		if (balance.compareTo(order.getTotalAmount()) <= 0) {
			throw new RuntimeException("余额不足！");
		}
		accountClient.paymentWithNested(buildAccountNestedDTO(order));
		return "success";
	}

	public String makePaymentWithNestedException(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAYING);
		final BigDecimal balance = accountClient.findByUserId(order.getUserId());
		if (balance.compareTo(order.getTotalAmount()) <= 0) {
			throw new RuntimeException("余额不足！");
		}
		accountClient.paymentWithNestedException(buildAccountNestedDTO(order));
		return "success";
	}

	public void confirmOrderStatus(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAY_SUCCESS);
		log.info("=========进行订单confirm操作完成================");
	}

	public void cancelOrderStatus(OrderEntity order) {
		updateOrderStatus(order, OrderStatus.PAY_FAIL);
		log.info("=========进行订单cancel操作完成================");
	}

	private void updateOrderStatus(OrderEntity order, OrderStatus orderStatus) {
		order.setStatus(orderStatus.getCode());
		orderMapper.update(null, new LambdaUpdateWrapper<OrderEntity>().eq(OrderEntity::getNumber, order.getNumber())
				.set(OrderEntity::getStatus, order.getStatus()));
	}

	private AccountDTO buildAccountDTO(OrderEntity order) {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAmount(order.getTotalAmount());
		accountDTO.setUserId(order.getUserId());
		return accountDTO;
	}

	private InventoryDTO buildInventoryDTO(OrderEntity order) {
		InventoryDTO inventoryDTO = new InventoryDTO();
		inventoryDTO.setCount(order.getTotalCount());
		inventoryDTO.setProductId(order.getProductId());
		return inventoryDTO;
	}

	private AccountNestedDTO buildAccountNestedDTO(OrderEntity order) {
		AccountNestedDTO nestedDTO = new AccountNestedDTO();
		nestedDTO.setAmount(order.getTotalAmount());
		nestedDTO.setUserId(order.getUserId());
		nestedDTO.setProductId(order.getProductId());
		nestedDTO.setCount(order.getTotalCount());
		return nestedDTO;
	}
}
