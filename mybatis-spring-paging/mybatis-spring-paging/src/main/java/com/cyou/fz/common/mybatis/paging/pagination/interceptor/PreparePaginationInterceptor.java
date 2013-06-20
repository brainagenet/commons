package com.cyou.fz.common.mybatis.paging.pagination.interceptor;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.cyou.fz.common.mybatis.paging.pagination.page.Page;
import com.cyou.fz.common.mybatis.paging.util.ReflectionUtility;

import java.sql.Connection;
import java.util.Properties;

/**
 * <p>
 * Mybatis数据库分页插件.
 * 拦截StatementHandler的prepare方法
 * </p>
 *
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PreparePaginationInterceptor extends BaseInterceptor {
    /**
     * 序列化
     */
    private static final long serialVersionUID = -6075937069117597841L;

    public PreparePaginationInterceptor() {
        super();
    }

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            final RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            final BaseStatementHandler delegate = (BaseStatementHandler) ReflectionUtility.getFieldValue(statementHandler, DELEGATE);
            final MappedStatement mappedStatement = (MappedStatement) ReflectionUtility.getFieldValue(delegate, MAPPED_STATEMENT);

            if (mappedStatement.getId().matches(_SQL_PATTERN)) { //拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                //分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject == null) {
                	logger.error("参数未实例化");
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    final Connection connection = (Connection) ivk.getArgs()[0];
                    final String sql = boundSql.getSql();
                    //记录统计
                    final int count = SQLHelp.getCount(sql, connection,
                            mappedStatement, parameterObject, boundSql);
                    Page page = null;
                    page = convertParameter(parameterObject, page);
                    page.init(count, page.getPageSize(), page.getPageNo());
                    String pagingSql = SQLHelp.generatePageSql(sql, page, DIALECT);
                    if (logger.isDebugEnabled()) {
                        logger.debug("分页SQL:" + pagingSql);
                    }
                    //将分页sql语句反射回BoundSql.
                    ReflectionUtility.setFieldValue(boundSql, "sql", pagingSql);
                }
            }
        }
        return ivk.proceed();
    }


    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        initProperties(properties);
    }
}
