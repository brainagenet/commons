package com.cyou.fz.common.mybatis.paging.pagination.page;

import java.io.Serializable;

/**
 * <p>
 * 分页接口.
 * </p>
 *
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public interface Page extends Serializable {

    int getPageNo();

    boolean isNext();

    boolean isPrevious();

    int getPageEndRow();

    int getPageSize();

    int getPageStartRow();

    int getTotalPages();

    int getTotalHit();

    void setTotalPages(int i);

    void setPageNo(int i);

    void setNext(boolean b);

    void setPrevious(boolean b);

    void setPageEndRow(int i);

    void setPageSize(int i);

    void setPageStartRow(int i);

    void setTotalHit(int i);

    void init(int rows, int pageSize, int currentPage);
}
