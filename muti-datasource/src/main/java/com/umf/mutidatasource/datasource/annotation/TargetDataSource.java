package com.umf.mutidatasource.datasource.annotation;

import java.lang.annotation.*;

/**
 * @author Stephen
 * @Title: TargetDataSource
 * @Package com.umf.mutidatasource.datasource.annotation
 * @date 2018/5/24 21:58
 */
@Target({ElementType.METHOD, ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
	String value() default ""; //该值即key值
}