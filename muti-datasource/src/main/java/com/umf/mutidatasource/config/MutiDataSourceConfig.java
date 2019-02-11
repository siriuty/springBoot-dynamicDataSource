package com.umf.mutidatasource.config;

import com.umf.mutidatasource.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * springboot集成mybatis的基本入口 1）创建数据源(如果采用的是默认的tomcat-jdbc数据源，则不需要)
 * 2）创建SqlSessionFactory 3）配置事务管理器，除非需要使用事务，否则不用配置
 * @author Stephen
 */
@Configuration
public class MutiDataSourceConfig implements EnvironmentAware {
	protected final Log logger = LogFactory.getLog(this.getClass());
	/**别名*/
	private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

	static {
		//由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
		aliases.addAliases("url", new String[]{"jdbc-url"});
		aliases.addAliases("username", new String[]{"user"});
	}
	/**配置上下文（也可以理解为配置文件的获取工具）*/
	private Environment evn;

	private static final Map<Object, Object> sourceMap = new HashMap<Object, Object>(5);
	/**参数绑定工具*/
	private Binder binder;

	/**
	 * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
	 * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）
	 */
	@Bean
	@Primary
	public DynamicDataSource dataSource() {
		//获取默认数据源配置
		Map<String,Object> defaultConfig = binder.bind("spring.datasource", Map.class).get();
		//默认数据源类型
		String typeStr = evn.getProperty("spring.datasource.type");
		//获取数据源类型
		Class<? extends DataSource> clazz = getDataSourceType(typeStr);
		//绑定默认数据源参数
		DataSource defaultDatasource = bind(clazz, defaultConfig);
		String names = evn.getProperty("custom.datasource.names");
		if(!StringUtils.isEmpty(names)){
			for (String name:names.split(",")) {
				Map<String,Object> properties=null;
				try{
					Map<String,Object> customerConfig = binder.bind("custom.datasource."+name, Map.class).get();
					clazz = getDataSourceType((String) customerConfig.get("type"));
					//获取extend字段，未定义或为true则为继承状态
					if ((boolean) customerConfig.getOrDefault("extend", Boolean.TRUE)) {
						//继承默认数据源配置
						properties = new HashMap(defaultConfig);
						//添加数据源参数
						properties.putAll(customerConfig);
					} else {
						//不继承默认配置
						properties = customerConfig;
					}
					//获取其他数据源配置
					//绑定参数
					DataSource consumerDatasource = bind(clazz, properties);
					//获取数据源的key，以便通过该key可以定位到数据源
					sourceMap.put(name, consumerDatasource);
				}catch (Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}
		DynamicDataSource dataSource = new DynamicDataSource();
		// 该方法是AbstractRoutingDataSource的方法
		dataSource.setTargetDataSources(sourceMap);
		// 默认的datasource设置为myTestDbDataSource
		dataSource.setDefaultTargetDataSource(defaultDatasource);
		return dataSource;
	}
	/**
	 * 根据数据源创建SqlSessionFactory
	 */
	@Bean
	public SqlSessionFactory getSqlSessionFactory(DynamicDataSource ds ) throws Exception {
		SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
		// 指定数据源(这个必须有，否则报错)
		fb.setDataSource(ds);
		// 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
		// 指定基包
		fb.setTypeAliasesPackage(evn.getProperty("mybatis.typeAliasesPackage"));
		fb.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(evn.getProperty("mybatis.mapperLocations")));
		return fb.getObject();
	}
	@Bean
	public SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	/**
	 * 配置事务管理器
	 */
	@Bean
	public DataSourceTransactionManager transactionManager(DynamicDataSource dataSource) throws Exception {
		return new DataSourceTransactionManager(dataSource);
	}

	/**
	 * EnvironmentAware接口的实现方法，通过aware的方式注入，此处是environment对象
	 *
	 * @param environment
	 */
	@Override
	public void setEnvironment(Environment environment) {
		this.evn = environment;
		//绑定配置器
		binder = Binder.get(evn);
	}

	private <T extends DataSource> T bind(Class<T> clazz, Map properties) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
		Binder binder = new Binder(new ConfigurationPropertySource[]{source.withAliases(aliases)});
		//通过类型绑定参数并获得实例对象
		return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(clazz)).get();
	}
	/**
	 * 通过字符串获取数据源class对象
	 *
	 * @param typeStr
	 * @return
	 */
	private Class<? extends DataSource> getDataSourceType(String typeStr) {
		Class<? extends DataSource> type;
		try {
			if (StringUtils.hasLength(typeStr)) {
				//字符串不为空则通过反射获取class对象
				type = (Class<? extends DataSource>) Class.forName(typeStr);
			} else {
				//默认为hikariCP数据源，与springboot默认数据源保持一致
				type = HikariDataSource.class;
			}
			return type;
		} catch (Exception e) {
			//无法通过反射获取class对象的情况则抛出异常，该情况一般是写错了，所以此次抛出一个runtimeexception
			throw new IllegalArgumentException("can not resolve class with type: " + typeStr);
		}
	}
}