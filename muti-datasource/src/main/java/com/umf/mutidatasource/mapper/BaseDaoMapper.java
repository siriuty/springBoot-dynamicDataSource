package com.umf.mutidatasource.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Stephen
 * @Title: BaseDaoMapper
 * @Package com.umf.mutidatasource.mapper
 * @date 2018/5/15 7:18
 */
public interface BaseDaoMapper<T> extends Mapper<T>, MySqlMapper<T> {
	//TODO
	//FIXME 特别注意，该接口不能被扫描到，否则会出错
}
