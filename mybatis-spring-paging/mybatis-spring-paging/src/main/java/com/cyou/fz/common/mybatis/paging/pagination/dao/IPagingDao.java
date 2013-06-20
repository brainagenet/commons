/**
 * 北京畅游时空软件技术有限公司福州分公司 - 版权所有
 * 2013-5-3 下午5:44:53
 */
package com.cyou.fz.common.mybatis.paging.pagination.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.cyou.fz.common.mybatis.MyBatisRepository;

/**
 * @author zhufu
 * 
 */
@MyBatisRepository
public interface IPagingDao {

	// int count(@Param("sql")String sql);
	/**
	 * 获得总数.
	 * 
	 * @param param
	 *            包含被执行的查询语句的参数，以及查询语句本身.
	 * @return 总数
	 * @author zhufu 2013-6-8 下午12:14:09 动作:新建
	 */
	@Select(value = "select count(1) from (${sql})")
	int count(Map<String, Object> param);
}
