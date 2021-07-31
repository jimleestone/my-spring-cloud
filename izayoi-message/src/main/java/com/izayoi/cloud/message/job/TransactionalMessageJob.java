package com.izayoi.cloud.message.job;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;

import com.izayoi.cloud.message.service.TransactionalMessageManagementService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionalMessageJob {

	private final TransactionalMessageManagementService managementService;

	private final RedissonClient redisson;

	@Scheduled(fixedDelay = 10000)
	public void transactionalMessageCompensationTask() throws Exception {
		RLock lock = redisson.getLock("transactionalMessageCompensationTask");
		// 等待时间5秒,预期300秒执行完毕,这两个值需要按照实际场景定制
		boolean tryLock = lock.tryLock(5, 300, TimeUnit.SECONDS);
		if (tryLock) {
			try {
				long start = System.currentTimeMillis();
				managementService.processPendingCompensationRecords();
				long end = System.currentTimeMillis();
				long delta = end - start;
				// 以防锁过早释放
				if (delta < 5000) {
					Thread.sleep(5000 - delta);
				}
			} finally {
				lock.unlock();
			}
		}
	}
}
