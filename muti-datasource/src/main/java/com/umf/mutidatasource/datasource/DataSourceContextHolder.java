package com.umf.mutidatasource.datasource;

import java.util.Set;

/**
 * @author Stephen
 * @Title: DataSourceContextHolder
 * @Package com.umf.mutidatasource.datasource
 * @date 2018/5/24 21:31
 * 保存全部自定义数据源名称
 */
public class DataSourceContextHolder {
	//保存当前线程的数据源对应的key
	private final static ThreadLocal<String> DATA_SOURCE_KEY = new ThreadLocal<>();
	/**所有数据源的key集合*/
	private static Set<Object> keySet;

	public static void setDataSourceKey(String key) {
		if(isContainDataSource(key)) {
			//切换当先线程的key
			DATA_SOURCE_KEY.set(key);
		}
	}

	public static String getDataSourceKey() {
		return DATA_SOURCE_KEY.get();
	}

	public static void clear() {
		//移除key值
		DATA_SOURCE_KEY.remove();
	}

	public static boolean isContainDataSource(String dataSourceKey) {
		return keySet.contains(dataSourceKey);
	}

	public static void setKeySet(Set<Object> keySet) {
		DataSourceContextHolder.keySet = keySet;
	}
}