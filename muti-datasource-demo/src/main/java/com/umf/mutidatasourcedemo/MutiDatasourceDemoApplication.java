package com.umf.mutidatasourcedemo;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication(
		scanBasePackages = {"com.umf.*"},
		exclude = {
				DataSourceAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class
		}
)
@MapperScan({"com.umf.mutidatasourcedemo.mapper"})
public class MutiDatasourceDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MutiDatasourceDemoApplication.class, args);
	}
}
