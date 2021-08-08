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
	 * 扣减库存操作. 这一个tcc接口
	 *
	 * @param inventoryDTO 库存DTO对象
	 * @return true
	 */
	public Boolean decrease(InventoryDTO inventoryDTO) {
		log.info("==========try扣减库存decrease===========");
		doDecrease(inventoryDTO);
		return true;
	}

	public Boolean testDecrease(InventoryDTO inventoryDTO) {
		doTestDecrease(inventoryDTO);
		return true;
	}

	@Transactional
	public Boolean mockWithTryException(InventoryDTO inventoryDTO) {
		// 这里是模拟异常所以就直接抛出异常了
		throw new RuntimeException("库存扣减异常！");
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean mockWithTryTimeout(InventoryDTO inventoryDTO) {
		try {
			// 模拟延迟 当前线程暂停10秒
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("==========springcloud调用扣减库存mockWithTryTimeout===========");
		final int decrease = doDecrease(inventoryDTO);
		if (decrease != 1) {
			throw new RuntimeException("库存不足");
		}
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean confirmMethodTimeout(InventoryDTO inventoryDTO) {
		try {
			// 模拟延迟 当前线程暂停11秒
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("==========Springcloud调用扣减库存确认方法===========");
		doDecrease(inventoryDTO);
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public Boolean confirmMethodException(InventoryDTO inventoryDTO) {
		log.info("==========Springcloud调用扣减库存确认方法===========");
		final int decrease = doDecrease(inventoryDTO);
		if (decrease != 1) {
			throw new RuntimeException("库存不足");
		}
		return true;
		// throw new TccRuntimeException("库存扣减确认异常！");
	}

	public Boolean confirmMethod(InventoryDTO inventoryDTO) {
		log.info("==========confirmMethod库存确认方法===========");
		return doConfirm(inventoryDTO) > 0;
	}

	public Boolean cancelMethod(InventoryDTO inventoryDTO) {
		log.info("==========cancelMethod库存取消方法===========");
		return doCancel(inventoryDTO) > 0;
	}

	/**
	 * 获取商品库存信息.
	 *
	 * @param productId 商品id
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
