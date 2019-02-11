package com.umf.mutidatasourcedemo.mapper.ahstl;


import com.umf.mutidatasource.mapper.BaseDaoMapper;
import com.umf.mutidatasourcedemo.entity.SysUserEntity;

/**
 * @author ${user}
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/5/1522:52
 */
public interface AhstlUserMapper extends BaseDaoMapper<SysUserEntity> {
	public SysUserEntity selectByUserId(Long userId);
}
