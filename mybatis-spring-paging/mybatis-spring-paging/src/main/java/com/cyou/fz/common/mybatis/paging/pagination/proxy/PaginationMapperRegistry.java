package com.cyou.fz.common.mybatis.paging.pagination.proxy;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * <p>
 * PaginationMapperRegistry
 * </p>
 * 
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class PaginationMapperRegistry extends MapperRegistry {
	/**
	 * mybatis 配置.
	 */
	private Configuration config;

	public PaginationMapperRegistry(Configuration config) {
		super(config);
		this.config = config;
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		if (!hasMapper(type)) {
			throw new BindingException("Type " + type
					+ " is not known to the MapperRegistry.");
		}
		try {
			// 添加mybatis配置参数
			return PaginationMapperProxy.newMapperProxy(type, sqlSession,
					config);
		} catch (Exception e) {
			throw new BindingException("Error getting mapper instance. Cause: "
					+ e, e);
		}
	}
}
