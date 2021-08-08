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
		System.out.println("hmily-cloud�ֲ�ʽ�����ʱ��" + (System.currentTimeMillis() - start));
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
	 * ģ���ڶ���֧�������У������try�׶��е�timeout
	 *
	 * @param count  ��������
	 * @param amount ֧�����
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
		log.debug("������������");
		OrderEntity order = new OrderEntity();
		order.setCreateTime(LocalDateTime.now());
		order.setNumber(String.valueOf(IdWorkerUtils.getInstance().createUUID()));
		// demo�еı���ֻ����ƷidΪ 1������
		order.setProductId("1");
		order.setStatus(OrderStatus.NOT_PAY.getCode());
		order.setTotalAmount(amount);
		order.setTotalCount(count);
		// demo�� ���������û�idΪ10000
		order.setUserId("10000");

		return order;
	}
}
