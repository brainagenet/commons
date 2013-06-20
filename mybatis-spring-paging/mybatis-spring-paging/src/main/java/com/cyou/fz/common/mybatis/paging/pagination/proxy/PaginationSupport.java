package com.cyou.fz.common.mybatis.paging.pagination.proxy;

import com.cyou.fz.common.mybatis.paging.pagination.page.Pagination;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 分页对象.
 * </p>
 *
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class PaginationSupport<E> extends Pagination {
	private static final long serialVersionUID = 1451875979747005797L;

    private List<E> datas = Collections.emptyList();
    
    public PaginationSupport(int rows, int pageSize) {
        super(rows, pageSize);
    }

    public PaginationSupport() {
    	super();
    }

	public List<E> getDatas(){
		return datas;
	}

	public void setDatas(List<E> datas){
		this.datas = datas;
	}
    
}
