package com.izayoi.order.controller;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.izayoi.common.dto.Message;
import com.izayoi.common.dto.RequestData;
import com.izayoi.common.dto.ResponseData;
import com.izayoi.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

	private static Disposable disposable;

	private final OrderService orderService;
	private final RSocketRequester messageService;

	@GetMapping("/mono")
	Mono<ResponseData> getMono(@RequestParam String msg) {
		return messageService.route("getMono").data(new RequestData(msg)).retrieveMono(ResponseData.class)
				.doOnNext(m -> log.info("message {} found.", m));
	}

	@GetMapping("/flux")
	Flux<ResponseData> getFlux(@RequestParam String msg) {
		return messageService.route("getFlux").data(new RequestData(msg)).retrieveFlux(ResponseData.class)
				.doOnNext(m -> log.info("message {} found.", m));
	}

	@GetMapping("/channel")
	public void channel() {
		log.info(
				"\n\n***** Channel (bi-directional streams)\n***** Asking for a stream of messages.\n***** Type 's' to stop.\n\n");

		Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
		Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
		Mono<Duration> setting3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));

		Flux<Duration> settings = Flux.concat(setting1, setting2, setting3)
				.doOnNext(d -> log.info("\nSending setting for a {}-second interval.\n", d.getSeconds()));

		disposable = messageService.route("channel").data(settings).retrieveFlux(Message.class)
				.subscribe(message -> log.info("Received: {} \n(Type 's' to stop.)", message));

	}

	@GetMapping("/stop")
	public void stop() {
		log.info("Stopping the current stream.");
		disposable.dispose();
		log.info("Stream stopped.");

	}

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
