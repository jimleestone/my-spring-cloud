package com.izayoi.order.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.izayoi.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;

	// 订单支付接口（注意这里模拟的是创建订单并进行支付扣减库存等操作）
	@PostMapping(value = "/orderPay")
	public String orderPay(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPay(count, amount);
	}

	// 测试订单支付接口(这里是压测接口不添加分布式事务)
	@PostMapping(value = "/testOrderPay")
	public String testOrderPay(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		final long start = System.currentTimeMillis();
		String result = orderService.testOrderPay(count, amount);
		System.out.println("消耗时间为:" + (System.currentTimeMillis() - start));
		return result;
	}

	// 模拟下单付款操作在try阶段时候，库存异常，此时账户系统和订单状态会回滚，达到数据的一致性（注意:这里模拟的是系统异常，或者rpc异常）
	@PostMapping(value = "/mockInventoryWithTryException")
	public String mockInventoryWithTryException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockInventoryWithTryException(count, amount);
	}

	// 模拟下单付款操作在try阶段时候，库存超时异常（但是自身最后又成功了），此时账户系统和订单状态会回滚，（库存依赖事务日志进行恢复），达到数据的一致性（异常指的是超时异常）
	@PostMapping(value = "/mockInventoryWithTryTimeout")
	public String mockInventoryWithTryTimeout(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockInventoryWithTryTimeout(count, amount);
	}

	// 模拟下单付款操作在try阶段时候，账户rpc异常，此时订单状态会回滚，达到数据的一致性（注意:这里模拟的是系统异常，或者rpc异常）
	@PostMapping(value = "/mockAccountWithTryException")
	public String mockAccountWithTryException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockAccountWithTryException(count, amount);
	}

	// 模拟下单付款操作在try阶段时候，账户rpc超时异常（但是最后自身又成功了），此时订单状态会回滚，账户系统依赖自身的事务日志进行调度恢复，达到数据的一致性（异常指的是超时异常）
	@PostMapping(value = "/mockAccountWithTryTimeout")
	public String mockAccountWithTryTimeout(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockAccountWithTryTimeout(count, amount);
	}

	// 订单支付接口（这里模拟的是rpc的嵌套调用 order--> account--> inventory）
	@PostMapping(value = "/orderPayWithNested")
	public String orderPayWithNested(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPayWithNested(count, amount);
	}

	// 订单支付接口（里模拟的是rpc的嵌套调用 order--> account--> inventory, inventory异常情况
	@PostMapping(value = "/orderPayWithNestedException")
	public String orderPayWithNestedException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPayWithNestedException(count, amount);
	}
}
