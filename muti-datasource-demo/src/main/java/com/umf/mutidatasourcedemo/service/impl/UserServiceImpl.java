package com.umf.mutidatasourcedemo.service.impl;

import com.umf.mutidatasource.datasource.annotation.TargetDataSource;
import com.umf.mutidatasourcedemo.entity.SysUserEntity;
import com.umf.mutidatasourcedemo.mapper.ahstl.AhstlUserMapper;
import com.umf.mutidatasourcedemo.mapper.cmccpay.CmccpayUserMapper;
import com.umf.mutidatasourcedemo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ${user}
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 2018/5/1612:57
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	private static final Logger Log = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private AhstlUserMapper ahstluserMapper;
	@Autowired
	private CmccpayUserMapper cmccpayuserMapper;

	@Override
	public SysUserEntity getAhstlUserById(long id) {
		return ahstluserMapper.selectByUserId(id);
	}

	@Override
	@TargetDataSource("cmccpay")
	public SysUserEntity getCmccpayUserById(long id) {
		return cmccpayuserMapper.selectByUserId(id);
	}
}