package com.cyou.fz.common.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 类说明：用于注入Spring上下文ApplicationContext，以在应用程序中提供更为方便的控制.
 * 注意，只能该对象被配置到Spring中时才有效，需要在Spring的配置文件中进行配置.
 * 
 * <pre>
 * 范例：<bean class="com.cyou.fz.common.spring.SpringContextAware" />
 * </pre>
 * 
 * <pre>
 * 修改日期      修改人    修改原因
 * 2012-07-23   崔诗杰                    新建
 * </pre>
 */
public class SpringContextAware implements ApplicationContextAware {
	private static Logger logger = LoggerFactory
			.getLogger(SpringContextAware.class);

	/**
	 * 系统中的context对象
	 */
	private static ApplicationContext context = null;

	/**
	 * 
	 * 获取Spring上下文ApplicationContext对象
	 * 
	 * @return ApplicationContext对象
	 * 
	 *         <pre>
	 * 修改日期      修改人    修改原因
	 * 2012-07-23   崔诗杰                    新建
	 * </pre>
	 */
	public static ApplicationContext getContext() {
		if (context == null) {
			logger.error("当前context为空,可能是Spring配置文件中没有配置加载本类[{}]!",
					SpringContextAware.class.getName());
			throw new IllegalStateException(
					"当前没有Spring的applicationContext注入,请确定是否有配置Spring,并在Spring中配置了本类的注入!"
							+ SpringContextAware.class);
		}
		return context;
	}

	/**
	 * 取指定类型的Bean,如果不存在或存在多于1个,则抛出异常IllegalStateException.
	 * 
	 * @param <E>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getBeanByType(Class<? extends E> type) {
		try {
			String[] beanNames = getContext().getBeanNamesForType(type);
			if (beanNames != null && beanNames.length == 1) {
				return (E) getContext().getBean(beanNames[0]);
			}

			if (beanNames == null || beanNames.length == 0) {
				throw new IllegalStateException("未找到指定类型的Bean定义.");
			}

			throw new IllegalStateException("找到多个同类型的Bean定义.");

		} catch (Throwable th) {
			logger.error("根据类型在Spring上下文查找对象出错:" + type, th);
			throw new IllegalStateException("根据类型在Spring上下文查找对象出错:" + type);
		}
	}

	/**
	 * 
	 * 从Spring Context中获取指定的Bean
	 * 
	 * @param beanName
	 *            bean的名称
	 * @return bean对象
	 * 
	 *         <pre>
	 * 修改日期　　　修改人　　　修改原因
	 * 2012-07-23   崔诗杰                    新建
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	// 从Spring中取对象并转换是免不了有这错误的,所以忽略
	public static <E> E getBean(String beanName) {
		try {
			return (E) getContext().getBean(beanName);
		} catch (Throwable th) {
			// logger.error("在Spring上下文查找对象出错:" + beanName, th);
			throw new IllegalStateException("在Spring上下文查找对象出错:" + beanName);
		}
	}

	/**
	 * 从Spring Context中获取指定的Bean
	 * 
	 * @param <E>
	 * @param clazz
	 * @return
	 * 
	 *         <pre>
	 * 修改日期　　　修改人　　　修改原因
	 * 2012-07-23   崔诗杰                    新建
	 * </pre>
	 */
	public static <E> E getBean(Class<E> clazz) {
		return getBeanByType(clazz);
		// return getBean(clazz.getName());
	}

	/**
	 * 
	 * 是否有指定的Bean存在.
	 * 
	 * @param beanName
	 * @return
	 * 
	 *         <pre>
	 * 修改日期        修改人    修改原因
	 * 2012-07-23   崔诗杰                    新建
	 * </pre>
	 */
	public static boolean containBean(String beanName) {
		return getContext().containsBean(beanName);
	}

	/**
	 * 
	 * 用于在被Spring加载时，由Spring注入ApplicationContext对象
	 * 
	 * @param context
	 *            被注入的context对象
	 * @throws BeansException
	 *             <pre>
	 * 修改日期　　　修改人　　　修改原因
	 * 2012-07-23   崔诗杰                    新建
	 * </pre>
	 */
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		logger.debug("准备注入SpringContext[{}]", context.toString());

		if (SpringContextAware.context != null) {
			logger.warn("注意,已经注入过Spring上下文[{}],请检查配置是否有问题导致重复注入!",
					SpringContextAware.context.toString());
			// throw new
			// IllegalStateException("已经注册过Spring上下文,请检查配置是否有问题导致重复注入!");
		}
		SpringContextAware.context = context;
		
	}

}
