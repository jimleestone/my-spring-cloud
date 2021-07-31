package com.izayoi.cloud.message.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izayoi.cloud.message.entity.TransactionalMessage;

@Mapper
public interface TransactionalMessageMapper extends BaseMapper<TransactionalMessage> {

}
