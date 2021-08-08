package com.izayoi.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.izayoi.common.entity.OrderEntity;
import com.izayoi.common.enums.OrderStatus;
import com.izayoi.common.mapper.OrderMapper;
import com.izayoi.common.util.IdWorkerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

	private final OrderMapper orderMapper;

	private final PaymentService paymentService;

	public String orderPay(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		long start = System.currentTimeMillis();
		paymentService.makePayment(order);
		System.out.println("hmily-cloud分布式事务耗时：" + (System.currentTimeMillis() - start));
		return "success";
	}

	public String testOrderPay(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		paymentService.testMakePayment(order);
		return "success";
	}

	public String mockInventoryWithTryException(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.mockPaymentInventoryWithTryException(order);
	}

	public String mockAccountWithTryException(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.mockPaymentAccountWithTryException(order);
	}

	/**
	 * 模拟在订单支付操作中，库存在try阶段中的timeout
	 *
	 * @param count  购买数量
	 * @param amount 支付金额
	 * @return string
	 */
	public String mockInventoryWithTryTimeout(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.mockPaymentInventoryWithTryTimeout(order);
	}

	public String mockAccountWithTryTimeout(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.mockPaymentAccountWithTryTimeout(order);
	}

	public String orderPayWithNested(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.makePaymentWithNested(order);
	}

	public String orderPayWithNestedException(Integer count, BigDecimal amount) {
		OrderEntity order = saveOrder(count, amount);
		return paymentService.makePaymentWithNestedException(order);
	}

	public void updateOrderStatus(OrderEntity order) {
		orderMapper.update(null, new LambdaUpdateWrapper<OrderEntity>().eq(OrderEntity::getNumber, order.getNumber())
				.set(OrderEntity::getStatus, order.getStatus()));
	}

	private OrderEntity saveOrder(Integer count, BigDecimal amount) {
		final OrderEntity order = buildOrder(count, amount);
		orderMapper.insert(order);
		return order;
	}

	private OrderEntity buildOrder(Integer count, BigDecimal amount) {
		log.debug("构建订单对象");
		OrderEntity order = new OrderEntity();
		order.setCreateTime(LocalDateTime.now());
		order.setNumber(String.valueOf(IdWorkerUtils.getInstance().createUUID()));
		// demo中的表里只有商品id为 1的数据
		order.setProductId("1");
		order.setStatus(OrderStatus.NOT_PAY.getCode());
		order.setTotalAmount(amount);
		order.setTotalCount(count);
		// demo中 表里面存的用户id为10000
		order.setUserId("10000");

		return order;
	}
}
