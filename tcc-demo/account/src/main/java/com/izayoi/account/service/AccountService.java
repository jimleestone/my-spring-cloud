package com.izayoi.account.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.izayoi.account.client.InventoryClient;
import com.izayoi.common.dto.AccountDTO;
import com.izayoi.common.dto.AccountNestedDTO;
import com.izayoi.common.dto.InventoryDTO;
import com.izayoi.common.entity.AccountEntity;
import com.izayoi.common.mapper.AccountMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

	private final AccountMapper accountMapper;

	private final InventoryClient inventoryClient;

	public boolean payment(final AccountDTO accountDTO) {
		log.info("============ִ��try����ӿ�===============");
		update(accountDTO);
		return Boolean.TRUE;
	}

	public boolean testPayment(AccountDTO accountDTO) {
		testUpdate(accountDTO);
		return Boolean.TRUE;
	}

	public boolean mockWithTryException(AccountDTO accountDTO) {
		throw new RuntimeException("�˻��ۼ��쳣��");
	}

	public boolean mockWithTryTimeout(AccountDTO accountDTO) {
		try {
			// ģ���ӳ� ��ǰ�߳���ͣ10��
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int decrease = update(accountDTO);
		if (decrease != 1) {
			throw new RuntimeException("�˻�����");
		}
		return true;
	}

	public boolean paymentWithNested(AccountNestedDTO nestedDTO) {
		update(buildAccountDTO(nestedDTO));
		inventoryClient.decrease(buildInventoryDTO(nestedDTO));
		return Boolean.TRUE;
	}

	public boolean paymentWithNestedException(AccountNestedDTO nestedDTO) {
		update(buildAccountDTO(nestedDTO));
		inventoryClient.mockWithTryException(buildInventoryDTO(nestedDTO));
		return Boolean.TRUE;
	}

	/**
	 * Confirm boolean.
	 *
	 * @param accountDTO the account dto
	 * @return the boolean
	 */
	public boolean confirm(final AccountDTO accountDTO) {
		log.info("============ִ��confirm ����ӿ�===============");
		return doConfirm(accountDTO) > 0;
	}

	/**
	 * Cancel boolean.
	 *
	 * @param accountDTO the account dto
	 * @return the boolean
	 */
	public boolean cancel(final AccountDTO accountDTO) {
		log.info("============ִ��cancel ����ӿ�===============");
		return doCancel(accountDTO) > 0;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean confirmNested(AccountNestedDTO accountNestedDTO) {
		log.info("============confirmNestedȷ�ϸ���ӿ�===============");
		return doConfirm(buildAccountDTO(accountNestedDTO)) > 0;
	}

	/**
	 * Cancel nested boolean.
	 *
	 * @param accountNestedDTO the account nested dto
	 * @return the boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean cancelNested(AccountNestedDTO accountNestedDTO) {
		log.info("============cancelNested ִ��ȡ������ӿ�===============");
		return doCancel(buildAccountDTO(accountNestedDTO)) > 0;
	}

	public int update(AccountDTO accountDTO) {
		return accountMapper.update(null,
				new LambdaUpdateWrapper<AccountEntity>().eq(AccountEntity::getUserId, accountDTO.getUserId())
						.ge(AccountEntity::getBalance, accountDTO.getAmount())
						.set(AccountEntity::getUpdateTime, LocalDateTime.now())
						.setSql("balance = balance - " + accountDTO.getAmount())
						.setSql("freeze_amount= freeze_amount + " + accountDTO.getAmount()));
	}

	public int doConfirm(AccountDTO accountDTO) {
		return accountMapper.update(null,
				new LambdaUpdateWrapper<AccountEntity>().eq(AccountEntity::getUserId, accountDTO.getUserId())
						.ge(AccountEntity::getFreezeAmount, accountDTO.getAmount())
						.set(AccountEntity::getUpdateTime, LocalDateTime.now())
						.setSql("freeze_amount= freeze_amount - " + accountDTO.getAmount()));
	}

	public int doCancel(AccountDTO accountDTO) {
		return accountMapper.update(null,
				new LambdaUpdateWrapper<AccountEntity>().eq(AccountEntity::getUserId, accountDTO.getUserId())
						.ge(AccountEntity::getFreezeAmount, accountDTO.getAmount())
						.set(AccountEntity::getUpdateTime, LocalDateTime.now())
						.setSql("balance = balance + " + accountDTO.getAmount())
						.setSql("freeze_amount= freeze_amount - " + accountDTO.getAmount()));
	}

	public int testUpdate(AccountDTO accountDTO) {
		return accountMapper.update(null,
				new LambdaUpdateWrapper<AccountEntity>().eq(AccountEntity::getUserId, accountDTO.getUserId())
						.ge(AccountEntity::getBalance, accountDTO.getAmount())
						.set(AccountEntity::getUpdateTime, LocalDateTime.now())
						.setSql("balance = balance - " + accountDTO.getAmount()));
	}

	public AccountEntity findByUserId(final String userId) {
		return accountMapper.selectOne(new LambdaQueryWrapper<AccountEntity>().eq(AccountEntity::getUserId, userId));
	}

	private AccountDTO buildAccountDTO(AccountNestedDTO nestedDTO) {
		AccountDTO dto = new AccountDTO();
		dto.setAmount(nestedDTO.getAmount());
		dto.setUserId(nestedDTO.getUserId());
		return dto;
	}

	private InventoryDTO buildInventoryDTO(AccountNestedDTO nestedDTO) {
		InventoryDTO inventoryDTO = new InventoryDTO();
		inventoryDTO.setCount(nestedDTO.getCount());
		inventoryDTO.setProductId(nestedDTO.getProductId());
		return inventoryDTO;
	}
}
