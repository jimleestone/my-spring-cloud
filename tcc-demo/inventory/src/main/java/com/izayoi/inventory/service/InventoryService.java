package com.izayoi.inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.izayoi.common.dto.InventoryDTO;
import com.izayoi.common.entity.InventoryEntity;
import com.izayoi.common.mapper.InventoryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryMapper inventoryMapper;

	/**
	 * �ۼ�������. ��һ��tcc�ӿ�
	 *
	 * @param inventoryDTO ���DTO����
	 * @return true
	 */
	public Boolean decrease(InventoryDTO inventoryDTO) {
		log.info("==========try�ۼ����decrease===========");
		doDecrease(inventoryDTO);
		return true;
	}

	public Boolean testDecrease(InventoryDTO inventoryDTO) {
		doTestDecrease(inventoryDTO);
		return true;
	}

	@Transactional
	public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
		// ������ģ���쳣���Ծ�ֱ���׳��쳣��
		throw new RuntimeException("���ۼ��쳣��");
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
		try {
			// ģ���ӳ� ��ǰ�߳���ͣ10��
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("==========springcloud���ÿۼ����mockWithTryTimeout===========");
		final int decrease = doDecrease(inventoryDTO);
		if (decrease != 1) {
			throw new RuntimeException("��治��");
		}
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean confirmMethodTimeout(InventoryDTO inventoryDTO) {
		try {
			// ģ���ӳ� ��ǰ�߳���ͣ11��
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("==========Springcloud���ÿۼ����ȷ�Ϸ���===========");
		doDecrease(inventoryDTO);
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean confirmMethodException(InventoryDTO inventoryDTO) {
		log.info("==========Springcloud���ÿۼ����ȷ�Ϸ���===========");
		final int decrease = doDecrease(inventoryDTO);
		if (decrease != 1) {
			throw new RuntimeException("��治��");
		}
		return true;
		// throw new TccRuntimeException("���ۼ�ȷ���쳣��");
	}

	public Boolean confirmMethod(InventoryDTO inventoryDTO) {
		log.info("==========confirmMethod���ȷ�Ϸ���===========");
		return doConfirm(inventoryDTO) > 0;
	}

	public Boolean cancelMethod(InventoryDTO inventoryDTO) {
		log.info("==========cancelMethod���ȡ������===========");
		return doCancel(inventoryDTO) > 0;
	}

	/**
	 * ��ȡ��Ʒ�����Ϣ.
	 *
	 * @param productId ��Ʒid
	 * @return InventoryDO
	 */
	public InventoryEntity findByProductId(String productId) {
		return inventoryMapper
				.selectOne(new LambdaQueryWrapper<InventoryEntity>().eq(InventoryEntity::getProductId, productId));
	}

	public int doDecrease(InventoryDTO inventoryDTO) {
		return inventoryMapper.update(null,
				new LambdaUpdateWrapper<InventoryEntity>()
						.eq(InventoryEntity::getProductId, inventoryDTO.getProductId())
						.ge(InventoryEntity::getTotalInventory, inventoryDTO.getCount())
						.setSql("total_inventory = total_inventory - " + inventoryDTO.getCount())
						.setSql("lock_inventory = lock_inventory + " + inventoryDTO.getCount()));
	}

	public int doConfirm(InventoryDTO inventoryDTO) {
		return inventoryMapper.update(null,
				new LambdaUpdateWrapper<InventoryEntity>()
						.eq(InventoryEntity::getProductId, inventoryDTO.getProductId())
						.ge(InventoryEntity::getLockInventory, inventoryDTO.getCount())
						.setSql("lock_inventory = lock_inventory - " + inventoryDTO.getCount()));
	}

	public int doCancel(InventoryDTO inventoryDTO) {
		return inventoryMapper.update(null,
				new LambdaUpdateWrapper<InventoryEntity>()
						.eq(InventoryEntity::getProductId, inventoryDTO.getProductId())
						.ge(InventoryEntity::getLockInventory, inventoryDTO.getCount())
						.setSql("total_inventory = total_inventory + " + inventoryDTO.getCount())
						.setSql("lock_inventory = lock_inventory - " + inventoryDTO.getCount()));
	}

	public int doTestDecrease(InventoryDTO inventoryDTO) {
		return inventoryMapper.update(null,
				new LambdaUpdateWrapper<InventoryEntity>()
						.eq(InventoryEntity::getProductId, inventoryDTO.getProductId())
						.ge(InventoryEntity::getTotalInventory, inventoryDTO.getCount())
						.setSql("total_inventory = total_inventory - " + inventoryDTO.getCount()));
	}

}
