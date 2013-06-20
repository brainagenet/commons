/**
 * ===========================================
 * Project: cyou-framework
 * ===========================================
 * Package: com.cyou.framework.pagination.proxy
 * 
 * Copyright (c) 2012, CYOU All Rights Reserved.
 * ===========================================
 */
package com.cyou.fz.common.mybatis.paging.pagination.proxy;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import com.cyou.fz.common.mybatis.paging.util.ReflectionUtility;

/**
 * <p>MySqlSessionFactoryBuilder</p>
 *
 * @since 1.0
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class MySqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {
	
	@Override
	public SqlSessionFactory build(Configuration config) {
		ReflectionUtility.setFieldValue(config, "mapperRegistry", new PaginationMapperRegistry(config));
		return new DefaultSqlSessionFactory(config);
	}

}
