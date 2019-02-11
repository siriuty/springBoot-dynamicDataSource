package com.umf.mutidatasourcedemo.mapper.cmccpay;


import com.umf.mutidatasource.mapper.BaseDaoMapper;
import com.umf.mutidatasourcedemo.entity.SysUserEntity;

/**
 * @author ${user}
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/5/1522:52
 */
public interface CmccpayUserMapper extends BaseDaoMapper<SysUserEntity> {
	public SysUserEntity selectByUserId(Long userId);
}
