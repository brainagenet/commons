package com.cyou.fz.common.mybatis.paging.pagination.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * PaginationMapperProxy
 * 
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class PaginationMapperProxy implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 5018029243120226326L;
	private SqlSession sqlSession;
	private Configuration config;

	private <T> PaginationMapperProxy(final SqlSession sqlSession, Configuration config) {
		this.sqlSession = sqlSession;
		this.config = config;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			return method.invoke(this, args);
		}

		final Class<?> declaringInterface = findDeclaringInterface(proxy,
				method);

		if (PaginationSupport.class.isAssignableFrom(method.getReturnType())) {
			// 分页处理
			final PaginationMapperMethod paginationMapperMethod = new PaginationMapperMethod(
					declaringInterface, method, sqlSession);
			final Object paginationResult = paginationMapperMethod
					.execute(args);
			return paginationResult;
		}

		// 原处理方式
		// 升级到mybatis3.2 删除原来传入的参数 sqlSession移到execute中传入.
		// 添加configuration
		final MapperMethod mapperMethod = new MapperMethod(declaringInterface,
				method, config);
		final Object result = mapperMethod.execute(sqlSession, args);

		if (result == null && method.getReturnType().isPrimitive()
				&& !method.getReturnType().equals(Void.TYPE)) {
			throw new BindingException(
					"Mapper method '"
							+ method.getName()
							+ "' ("
							+ method.getDeclaringClass()
							+ ") attempted to return null from a method with a primitive return type ("
							+ method.getReturnType() + ").");
		}
		return result;
	}

	private Class<?> findDeclaringInterface(Object proxy, Method method) {
		Class<?> declaringInterface = null;
		for (Class<?> mapperFaces : proxy.getClass().getInterfaces()) {
			try {
				Method m = mapperFaces.getMethod(method.getName(),
						method.getParameterTypes());
				if (declaringInterface != null) {
					throw new BindingException(
							"Ambiguous method mapping.  Two mapper interfaces contain the identical method signature for "
									+ method);
				}
				if (m != null) {
					declaringInterface = mapperFaces;
				}
			} catch (Exception e) {
			}
		}
		if (declaringInterface == null) {
			throw new BindingException(
					"Could not find interface with the given method " + method);
		}
		return declaringInterface;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newMapperProxy(Class<T> mapperInterface,
			SqlSession sqlSession, Configuration config) {
		ClassLoader classLoader = mapperInterface.getClassLoader();
		Class<?>[] interfaces = new Class[] { mapperInterface };
		PaginationMapperProxy proxy = new PaginationMapperProxy(sqlSession, config);
		return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
	}
}
