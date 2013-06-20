package com.cyou.fz.common.mybatis.paging.pagination.proxy;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 执行代理类，扩展Mybatis的方式来让其Mapper接口来支持.
 * </p>
 *
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class PaginationMapperMethod {

	private final SqlSession sqlSession;
	private final Configuration config;

	private SqlCommandType type;
	private String commandName;

	private Class<?> declaringInterface;
	private Method method;

	private Integer rowBoundsIndex;
	private List<String> paramNames;
	private List<Integer> paramPositions;

	private boolean hasNamedParameters;

	public PaginationMapperMethod(Class<?> declaringInterface, Method method,
			SqlSession sqlSession){
		paramNames = new ArrayList<String>();
		paramPositions = new ArrayList<Integer>();
		this.sqlSession = sqlSession;
		this.method = method;
		this.config = sqlSession.getConfiguration();
		this.hasNamedParameters = false;
		this.declaringInterface = declaringInterface;
		setupFields();
		setupMethodSignature();
		setupCommandType();
		validateStatement();
	}

	public Object execute(Object[] args){
		return executeForPage(args);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <E> PaginationSupport<E> executeForPage(Object[] args){
		PaginationSupport<E> page = null;
		
		List<E> result;
		Object param = getParam(args);
		if (rowBoundsIndex != null) {
			RowBounds rowBounds = (RowBounds) args[rowBoundsIndex];
			result = sqlSession.<E> selectList(commandName, param, rowBounds);
		} else {
			result = sqlSession.<E> selectList(commandName, param);
		}
		
		Map map = (Map)param;
		page = (PaginationSupport<E>) map.get("page");
		
		page.setDatas(result);
		
		return page;
	}

	private Object getParam(Object[] args){
		final int paramCount = paramPositions.size();
		if (args == null || paramCount == 0) {
			return null;
		} else if (!hasNamedParameters && paramCount == 1) {
			return args[paramPositions.get(0)];
		} else {
			Map<String, Object> param = new MapperParamMap<Object>();
			for (int i = 0; i < paramCount; i++) {
				param.put(paramNames.get(i), args[paramPositions.get(i)]);
			}
			// issue #71, add param names as param1, param2...but ensure backward compatibility
			for (int i = 0; i < paramCount; i++) {
				String genericParamName = "param" + String.valueOf(i + 1);
				if (!param.containsKey(genericParamName)) {
					param.put(genericParamName, args[paramPositions.get(i)]);
				}
			}
			return param;
		}
	}

	// Setup //

	private void setupFields(){
		this.commandName = declaringInterface.getName() + "."
				+ method.getName();
	}

	private void setupMethodSignature() {
		final Class<?>[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {
			if (RowBounds.class.isAssignableFrom(argTypes[i])) {
				if (rowBoundsIndex == null) {
					rowBoundsIndex = i;
				} else {
					throw new BindingException(method.getName() + " cannot have multiple RowBounds parameters");
				}
			} else {
				String paramName = String.valueOf(paramPositions.size());
				paramName = getParamNameFromAnnotation(i, paramName);
				paramNames.add(paramName);
				paramPositions.add(i);
			}
		}
	}

	private String getParamNameFromAnnotation(int i, String paramName){
		Object[] paramAnnos = method.getParameterAnnotations()[i];
		for (int j = 0; j < paramAnnos.length; j++) {
			if (paramAnnos[j] instanceof Param) {
				hasNamedParameters = true;
				paramName = ((Param) paramAnnos[j]).value();
			}
		}
		return paramName;
	}

	private void setupCommandType(){
		MappedStatement ms = config.getMappedStatement(commandName);
		type = ms.getSqlCommandType();
		if (type != SqlCommandType.SELECT) {
			throw new BindingException("Unsupport execution method for: " + commandName);
		}
	}

	private void validateStatement(){
		try {
			config.getMappedStatement(commandName);
		} catch (Exception e) {
			throw new BindingException("Invalid bound statement (not found): " + commandName, e);
		}
	}

	public static class MapperParamMap<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -2212268410512043556L;

		@Override
		public V get(Object key){
			if (!super.containsKey(key)) {
				throw new BindingException("Parameter '" + key
						+ "' not found. Available parameters are " + this.keySet());
			}
			return super.get(key);
		}

	}

}
