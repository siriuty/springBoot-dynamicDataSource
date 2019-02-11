package com.umf.mutidatasourcedemo;

import com.umf.mutidatasourcedemo.entity.SysUserEntity;
import com.umf.mutidatasourcedemo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MutiDatasourceDemoApplication.class)
public class MutiDatasourceDemoApplicationTests {
	@Autowired
	private UserService userService;
	@Test
	public void contextLoads() {
	}
	@Test
	public void userServiceTest(){
		SysUserEntity user = userService.getAhstlUserById(1L);
		System.out.println(user.getEmail());
	}

}
