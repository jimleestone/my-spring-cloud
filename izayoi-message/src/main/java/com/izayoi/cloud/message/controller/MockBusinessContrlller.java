package com.izayoi.cloud.message.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izayoi.cloud.message.service.MockBusinessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MockBusinessContrlller {

	private final MockBusinessService mockBusinessService;

	@GetMapping("/order")
	public String order() throws Exception {
		mockBusinessService.saveOrder();
		return "下单成功";
	}
}
