package com.cyou.fz.common.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * MyBatisRepository
 * </p>
 * <p>
 * @description 标识MyBatis的DAO,方便
 *              {@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。
 *              </p>
 * 
 * @author Dipin
 * @date 2013-4-1 上午9:26:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyBatisRepository {

}
