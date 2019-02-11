package com.umf.mutidatasource.datasource;

import com.umf.mutidatasource.datasource.annotation.TargetDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Stephen
 * @Title: DataSourceSwitchInterceptor
 * @Package com.umf.mutidatasource.datasource
 * @Description: 数据源切换拦截器
 * @date 2018/5/24 21:59
 */
@Component
@Aspect
@Order(-10)  //使该切面在事务之前执行
public class DataSourceSwitchInterceptor {
	/**
	 * 扫描所有含有@TargetDataSource注解的类
	 */
	@Pointcut("@annotation(com.umf.mutidatasource.datasource.annotation.TargetDataSource)")
	public void switchDataSource() {
	}

	/**
	 * 使用around方式监控
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("switchDataSource()")
	public Object switchByMethod(ProceedingJoinPoint point) throws Throwable {
		//获取执行方法
		Method method = getMethodByPoint(point);
		//获取执行参数
		Parameter[] params = method.getParameters();
		Parameter parameter;
		String source = null;
		boolean isDynamic = false;
		for (int i = params.length - 1; i >= 0; i--) {
			//扫描是否有参数带有@DataSource注解
			parameter = params[i];
			if (parameter.getAnnotation(TargetDataSource.class) != null && point.getArgs()[i] instanceof String) {
				//key值即该参数的值，要求该参数必须为String类型
				source = (String) point.getArgs()[i];
				isDynamic = true;
				break;
			}
		}
		if (!isDynamic) {
			//不存在参数带有Datasource注解
			//获取方法的@DataSource注解
			TargetDataSource dataSource = method.getAnnotation(TargetDataSource.class);
			//方法不含有注解
			if (null == dataSource || !StringUtils.hasLength(dataSource.value())) {
				//获取类级别的@DataSource注解
				dataSource = method.getDeclaringClass().getAnnotation(TargetDataSource.class);
			}
			if (null != dataSource) {
				//设置key值
				source = dataSource.value();
			}
		}
		//继续执行该方法
		return persistBySource(source, point);
	}


	private Object persistBySource(String source, ProceedingJoinPoint point) throws Throwable {
		try {
			//切换数据源
			DataSourceContextHolder.setDataSourceKey(source);
			return point.proceed();
		} finally {
			//清空key值
			DataSourceContextHolder.clear();
		}
	}

	private Method getMethodByPoint(ProceedingJoinPoint point) {
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		return methodSignature.getMethod();
	}
}