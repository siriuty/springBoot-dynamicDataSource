package com.umf.mutidatasourcedemo.service;


import com.umf.mutidatasourcedemo.entity.SysUserEntity;

/**
 * @author ${user}
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/5/1612:54
 */
public interface UserService {
	SysUserEntity getAhstlUserById(long id);
	SysUserEntity getCmccpayUserById(long id);
}
