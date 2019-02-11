package com.umf.mutidatasource.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Stephen
 * @Title: DynamicDataSource
 * @Package com.umf.mutidatasource.datasource
 * @date 2018/5/24 21:23
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		String key = DataSourceContextHolder.getDataSourceKey();
		if(!DataSourceContextHolder.isContainDataSource(key)){
			logger.info(String.format("can not found datasource by key: '%s',this session may use default datasource", key));
		}
		return key;
	}
	/**
	 * 在获取key的集合，目的只是为了添加一些告警日志
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		try {
			Field sourceMapField = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
			sourceMapField.setAccessible(true);
			Map<Object, DataSource> sourceMap = (Map<Object, DataSource>) sourceMapField.get(this);
			DataSourceContextHolder.setKeySet(sourceMap.keySet());
			sourceMapField.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			logger.error(e.getMessage(),e);
		}

	}
}