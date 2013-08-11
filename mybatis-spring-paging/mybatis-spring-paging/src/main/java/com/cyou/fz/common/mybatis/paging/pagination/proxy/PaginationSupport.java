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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PaginationSupport [datas=");
		builder.append(datas);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", totalPages=");
		builder.append(totalPages);
		builder.append(", totalHit=");
		builder.append(totalHit);
		builder.append(", pageStartRow=");
		builder.append(pageStartRow);
		builder.append(", pageEndRow=");
		builder.append(pageEndRow);
		builder.append(", next=");
		builder.append(isNext());
		builder.append(", previous=");
		builder.append(isPrevious());
		builder.append("]");
		return builder.toString();
	}
    
}
