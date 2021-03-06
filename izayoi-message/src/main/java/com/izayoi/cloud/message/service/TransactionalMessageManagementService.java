package com.izayoi.cloud.message.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.izayoi.cloud.message.common.TxMessageStatus;
import com.izayoi.cloud.message.entity.TransactionalMessage;
import com.izayoi.cloud.message.entity.TransactionalMessageContent;
import com.izayoi.cloud.message.mapper.TransactionalMessageContentMapper;
import com.izayoi.cloud.message.mapper.TransactionalMessageMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionalMessageManagementService {

	private final TransactionalMessageMapper messageMapper;
	private final TransactionalMessageContentMapper contentMapper;
	private final RabbitTemplate rabbitTemplate;

	private static final LocalDateTime END = LocalDateTime.of(2999, 1, 1, 0, 0, 0);
	private static final long DEFAULT_INIT_BACKOFF = 10L;
	private static final int DEFAULT_BACKOFF_FACTOR = 2;
	private static final int DEFAULT_MAX_RETRY_TIMES = 5;
	private static final int LIMIT = 100;

	public void saveTransactionalMessageRecord(TransactionalMessage record, String content) {
		record.setMessageStatus(TxMessageStatus.PENDING.getStatus());
		record.setNextScheduleTime(
				calculateNextScheduleTime(LocalDateTime.now(), DEFAULT_INIT_BACKOFF, DEFAULT_BACKOFF_FACTOR, 0));
		record.setCurrentRetryTimes(0);
		record.setInitBackoff(DEFAULT_INIT_BACKOFF);
		record.setBackoffFactor(DEFAULT_BACKOFF_FACTOR);
		record.setMaxRetryTimes(DEFAULT_MAX_RETRY_TIMES);
		messageMapper.insert(record);

		TransactionalMessageContent messageContent = new TransactionalMessageContent();
		messageContent.setContent(content);
		messageContent.setMessageId(record.getId());
		contentMapper.insert(messageContent);
	}

	public void sendMessageSync(TransactionalMessage record, String content) {
		try {
			rabbitTemplate.convertAndSend(record.getExchangeName(), record.getRoutingKey(), content);
			log.info("??????????????????, ????????????: {}, ????????????: {}", record.getQueueName(), content);

			// ????????????
			markSuccess(record);
		} catch (Exception e) {
			// ????????????
			markFail(record, e);
		}
	}

	private void markSuccess(TransactionalMessage record) {
		// ???????????????????????????????????????
		record.setNextScheduleTime(END);
		record.setMessageStatus(TxMessageStatus.SUCCESS.getStatus());
		messageMapper.updateById(record);
	}

	private void markFail(TransactionalMessage record, Exception e) {
		log.error("??????????????????, ????????????: {}, ??????: {}", record.getQueueName(), e);
		record.setCurrentRetryTimes(
				record.getCurrentRetryTimes().compareTo(record.getMaxRetryTimes()) >= 0 ? record.getMaxRetryTimes()
						: record.getCurrentRetryTimes() + 1);
		// ??????????????????????????????
		LocalDateTime nextScheduleTime = calculateNextScheduleTime(record.getNextScheduleTime(),
				record.getInitBackoff(), record.getBackoffFactor(), record.getCurrentRetryTimes());
		record.setNextScheduleTime(nextScheduleTime);
		record.setMessageStatus(TxMessageStatus.FAIL.getStatus());
		messageMapper.updateById(record);
	}

	/**
	 * ???????????????????????????
	 *
	 * @param base          ????????????
	 * @param initBackoff   ???????????????
	 * @param backoffFactor ????????????
	 * @param round         ??????
	 * @return LocalDateTime
	 */
	private LocalDateTime calculateNextScheduleTime(LocalDateTime base, long initBackoff, long backoffFactor,
			long round) {
		double delta = initBackoff * Math.pow(backoffFactor, round);
		LocalDateTime next = base.plusSeconds((long) delta);
		log.info("????????????????????????: {}", next);
		return next;
	}

	/**
	 * ???????????? - ?????????????????????????????????????????????
	 */
	public void processPendingCompensationRecords() {
		// ???????????????????????????????????????????????????????????????????????????????????????????????????
		LocalDateTime max = LocalDateTime.now().plusSeconds(-DEFAULT_INIT_BACKOFF);
		// ??????????????????????????????1??????
		LocalDateTime min = max.plusHours(-1);
		Map<Long, TransactionalMessage> collect = messageMapper
				.selectList(new LambdaQueryWrapper<TransactionalMessage>()
						.between(TransactionalMessage::getNextScheduleTime, min, max)
						.ne(TransactionalMessage::getMessageStatus, TxMessageStatus.SUCCESS.getStatus())
						.lt(TransactionalMessage::getCurrentRetryTimes, DEFAULT_MAX_RETRY_TIMES).last("limit " + LIMIT))
				.stream().collect(Collectors.toMap(TransactionalMessage::getId, x -> x));
		if (!collect.isEmpty()) {
			contentMapper.selectList(new LambdaQueryWrapper<TransactionalMessageContent>()
					.in(TransactionalMessageContent::getMessageId, collect.keySet())).forEach(item -> {
						TransactionalMessage message = collect.get(item.getMessageId());
						sendMessageSync(message, item.getContent());
					});
		}
	}

}
