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

	// ����֧���ӿڣ�ע������ģ����Ǵ�������������֧���ۼ����Ȳ�����
	@PostMapping(value = "/orderPay")
	public String orderPay(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPay(count, amount);
	}

	// ���Զ���֧���ӿ�(������ѹ��ӿڲ���ӷֲ�ʽ����)
	@PostMapping(value = "/testOrderPay")
	public String testOrderPay(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		final long start = System.currentTimeMillis();
		String result = orderService.testOrderPay(count, amount);
		System.out.println("����ʱ��Ϊ:" + (System.currentTimeMillis() - start));
		return result;
	}

	// ģ���µ����������try�׶�ʱ�򣬿���쳣����ʱ�˻�ϵͳ�Ͷ���״̬��ع����ﵽ���ݵ�һ���ԣ�ע��:����ģ�����ϵͳ�쳣������rpc�쳣��
	@PostMapping(value = "/mockInventoryWithTryException")
	public String mockInventoryWithTryException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockInventoryWithTryException(count, amount);
	}

	// ģ���µ����������try�׶�ʱ�򣬿�泬ʱ�쳣��������������ֳɹ��ˣ�����ʱ�˻�ϵͳ�Ͷ���״̬��ع������������������־���лָ������ﵽ���ݵ�һ���ԣ��쳣ָ���ǳ�ʱ�쳣��
	@PostMapping(value = "/mockInventoryWithTryTimeout")
	public String mockInventoryWithTryTimeout(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockInventoryWithTryTimeout(count, amount);
	}

	// ģ���µ����������try�׶�ʱ���˻�rpc�쳣����ʱ����״̬��ع����ﵽ���ݵ�һ���ԣ�ע��:����ģ�����ϵͳ�쳣������rpc�쳣��
	@PostMapping(value = "/mockAccountWithTryException")
	public String mockAccountWithTryException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockAccountWithTryException(count, amount);
	}

	// ģ���µ����������try�׶�ʱ���˻�rpc��ʱ�쳣��������������ֳɹ��ˣ�����ʱ����״̬��ع����˻�ϵͳ���������������־���е��Ȼָ����ﵽ���ݵ�һ���ԣ��쳣ָ���ǳ�ʱ�쳣��
	@PostMapping(value = "/mockAccountWithTryTimeout")
	public String mockAccountWithTryTimeout(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.mockAccountWithTryTimeout(count, amount);
	}

	// ����֧���ӿڣ�����ģ�����rpc��Ƕ�׵��� order--> account--> inventory��
	@PostMapping(value = "/orderPayWithNested")
	public String orderPayWithNested(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPayWithNested(count, amount);
	}

	// ����֧���ӿڣ���ģ�����rpc��Ƕ�׵��� order--> account--> inventory, inventory�쳣���
	@PostMapping(value = "/orderPayWithNestedException")
	public String orderPayWithNestedException(@RequestParam(value = "count") Integer count,
			@RequestParam(value = "amount") BigDecimal amount) {
		return orderService.orderPayWithNestedException(count, amount);
	}
}
