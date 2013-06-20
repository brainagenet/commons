package com.cyou.fz.common.mybatis.paging.pagination.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.fz.common.mybatis.paging.pagination.dao.IPagingDao;
import com.cyou.fz.common.mybatis.paging.pagination.page.Page;
import com.cyou.fz.common.mybatis.paging.pagination.page.PageContext;
import com.cyou.fz.common.spring.SpringContextAware;

/**
 * <p>
 * 数据库分页插件，只拦截查询语句.
 * </p>
 * 
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class,
		ResultHandler.class }) })
public class PaginationInterceptor extends BaseInterceptor {

	private static final long serialVersionUID = 3576678797374122941L;
	protected static Logger LOGGER = LoggerFactory
			.getLogger(PaginationInterceptor.class);

	private IPagingDao pagingDao;

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		int totPage;
		long begin = System.currentTimeMillis();
		long beginCount = 0;
		long endCount = 0;
		LOGGER.debug("Pagination Interceptor begin.");
		final MappedStatement mappedStatement = (MappedStatement) invocation
				.getArgs()[0];
		Object parameter = invocation.getArgs()[1];
		// 如果没有传参数 认为不是分页查询
		if (mappedStatement.getId().matches(_SQL_PATTERN) && parameter != null) { // 拦截需要分页的SQL
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			List<ParameterMapping> pmList = boundSql.getParameterMappings();
			String originalSql = boundSql.getSql().trim();
			// 从原生sql语句替换掉问号后的sql
			String generateSql = originalSql;
			// 替换问号为#{参数名}
			for (ParameterMapping parameterMapping : pmList) {
				String p = parameterMapping.getProperty();
				// Object value = ((Map<String, Object>)parameter).get(p);
				StringBuilder valueString = new StringBuilder();
				valueString.append("#{");
				valueString.append(p);
				valueString.append('}');
				generateSql = generateSql.replaceFirst("\\?",
						valueString.toString());
			}
			Object parameterObject = boundSql.getParameterObject();
			if (boundSql.getSql() == null || "".equals(boundSql.getSql()))
				return null;

			// 分页参数--上下文传参
			Page page = null;
			PageContext context = PageContext.getPageContext();

			// map传参每次都将currentPage重置,先判读map再判断context
			if (parameterObject != null) {
				page = convertParameter(parameterObject, page);
			}

			// 分页参数--context参数里的Page传参
			if (page == null) {
				page = context;
			}

			// 后面用到了context的东东
			if (page != null) {
				totPage = page.getTotalHit();
				// 得到总记录数
				LOGGER.debug("分页：计算总记录数 Begin...");
				if (totPage == 0) {
					beginCount = System.currentTimeMillis();
					// 添加参数sql参数值generateSql到传入参数中，分页查询一定是map参数传入
					HashMap<String, Object> parameterMap = (HashMap<String, Object>) parameterObject;
					parameterMap.put("sql", generateSql);
					pagingDao = SpringContextAware.getBean(IPagingDao.class);
					totPage = pagingDao.count(parameterMap);
					endCount = System.currentTimeMillis();
					// System.out.println(totPage);
				}
				LOGGER.debug("分页：计算总记录数 End.");

				// 分页计算
				page.init(totPage, page.getPageSize(), page.getPageNo());

				// 分页查询 本地化对象 修改数据库注意修改实现

				LOGGER.debug("分页：生成分页查询SQL Begin...");
				String pageSql = SQLHelp.generatePageSql(originalSql, page,
						DIALECT);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("分页SQL:" + pageSql);
				}
				LOGGER.debug("分页：生成分页查询SQL End...");
				invocation.getArgs()[2] = new RowBounds(
						RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
				BoundSql newBoundSql = new BoundSql(
						mappedStatement.getConfiguration(), pageSql,
						boundSql.getParameterMappings(),
						boundSql.getParameterObject());
				MappedStatement newMs = copyFromMappedStatement(
						mappedStatement, new BoundSqlSqlSource(newBoundSql));

				invocation.getArgs()[0] = newMs;
			}
		}
		long end = System.currentTimeMillis();
		LOGGER.debug("分页总共耗时: {}, count耗时: {}", (end - begin),
				(endCount - beginCount));
		// System.out.println("intercept : " + (end - begin));
		LOGGER.debug("Pagination Interceptor finish.");
		long beginInvoc = System.currentTimeMillis();
		Object o = invocation.proceed();
		long endInvoc = System.currentTimeMillis();
		LOGGER.debug("分页 查询 耗时: {}", endInvoc - beginInvoc);
		return o;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		super.initProperties(properties);
	}

	private MappedStatement copyFromMappedStatement(MappedStatement ms,
			SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(
				ms.getConfiguration(), ms.getId(), newSqlSource,
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			for (String keyProperty : ms.getKeyProperties()) {
				builder.keyProperty(keyProperty);
			}
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.cache(ms.getCache());
		return builder.build();
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
}
