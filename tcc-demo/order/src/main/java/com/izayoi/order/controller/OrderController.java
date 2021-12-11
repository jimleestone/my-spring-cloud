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
