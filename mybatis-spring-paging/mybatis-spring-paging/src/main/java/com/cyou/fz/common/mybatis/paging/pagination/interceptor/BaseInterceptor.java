package com.cyou.fz.common.mybatis.paging.pagination.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.fz.common.mybatis.paging.pagination.annotation.Paging;
import com.cyou.fz.common.mybatis.paging.pagination.dialect.Dialect;
import com.cyou.fz.common.mybatis.paging.pagination.page.Page;
import com.cyou.fz.common.mybatis.paging.pagination.page.Pagination;
import com.cyou.fz.common.mybatis.paging.util.ReflectionUtility;
import com.cyou.fz.common.mybatis.paging.util.StringUtility;

/**
 * <p>
 * 功能：
 * 1. 参数对象转换为Page对象。<br>
 * 2. 配置读取：dialectClass, sqlPattern, pageFieldName
 * </p>
 *
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {
	
    private static final long serialVersionUID = 4596430444388728543L;
    
    protected static Logger logger = LoggerFactory
			.getLogger(BaseInterceptor.class);


    protected static final String DELEGATE = "delegate";

    protected static final String MAPPED_STATEMENT = "mappedStatement";


    protected Dialect DIALECT;

    /**
     * 拦截的ID，在mapper中的id，可以匹配正则
     */
    protected String _SQL_PATTERN = "";
    
    protected static String MAP_PAGE_FIELD = "page";

    /**
     * 对参数进行转换和检查
     *
     * @param parameterObject 参数对象
     * @param pageVO          参数VO
     * @return 参数VO
     * @throws NoSuchFieldException 无法找到参数
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Page convertParameter(Object parameterObject, Page pageVO) throws NoSuchFieldException {
        if (parameterObject instanceof Page) {
            pageVO = (Pagination) parameterObject;
        } else if (parameterObject instanceof java.util.Map ) {
        	java.util.Map parameterMap = (java.util.Map) parameterObject;
        	pageVO = (Pagination) parameterMap.get(MAP_PAGE_FIELD);
        	if (pageVO == null) {
        		throw new PersistenceException("分页参数不能为空");
        	}
        	parameterMap.put(MAP_PAGE_FIELD, pageVO);
        } else {
            //参数为某个实体，该实体拥有Page属性
            Paging paging = parameterObject.getClass().getAnnotation(Paging.class);
            String field = paging.field();
            Field pageField = ReflectionUtility.getAccessibleField(parameterObject, field);
            if (pageField != null) {
                pageVO = (Pagination) ReflectionUtility.getFieldValue(parameterObject, field);
                if (pageVO == null) {
                	throw new PersistenceException("分页参数不能为空");
                }
                //通过反射，对实体对象设置分页对象
                ReflectionUtility.setFieldValue(parameterObject, field, pageVO);
            } else {
                throw new NoSuchFieldException(parameterObject.getClass().getName() + "不存在分页参数属性！");
            }
        }
        return pageVO;
    }

    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <p>
     * <code>dialectClass</code>,自定义方言类。<br>
     * <code>sqlPattern</code> 需要拦截的SQL ID
     * </p>
     *
     * @param p 属性
     */
    protected void initProperties(Properties p) {
        String dialectClass = p.getProperty("dialectClass");
        if (StringUtility.isEmpty(dialectClass)) {
            try {
                throw new PropertyException("数据库分页方言无法找到!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        } else {
            Dialect dialect1 = (Dialect) ReflectionUtility.instance(dialectClass);
            if (dialect1 == null) {
                throw new NullPointerException("方言实例错误");
            }
            DIALECT = dialect1;
        }

        _SQL_PATTERN = p.getProperty("sqlPattern");
        if (StringUtility.isEmpty(_SQL_PATTERN)) {
            try {
                throw new PropertyException("sqlPattern property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        
        String mapPageField = p.getProperty("mapPageField");
        if (StringUtility.isNotEmpty(mapPageField)) {
        	MAP_PAGE_FIELD = mapPageField;
        }
    }
}
