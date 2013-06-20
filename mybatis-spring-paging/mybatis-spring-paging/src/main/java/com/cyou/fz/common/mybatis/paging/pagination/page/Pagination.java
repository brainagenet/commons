package com.cyou.fz.common.mybatis.paging.pagination.page;

/**
 * <p>
 * 查询参数.
 * </p>
 * 
 * @since JDK 1.5
 * @version 1.0 2012-12-15
 * @author zhufu
 */
public class Pagination implements Page {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 8919076199499894558L;
	/**
	 * 每页默认10条数据
	 */
	protected int pageSize = 10;
	/**
	 * 当前页
	 */
	protected int pageNo = 1;
	/**
	 * 总页数
	 */
	protected int totalPages = 0;
	/**
	 * 总数据数
	 */
	protected int totalHit = 0;
	/**
	 * 每页的起始行数
	 */
	protected int pageStartRow = 0;
	/**
	 * 每页显示数据的终止行数
	 */
	protected int pageEndRow = 0;
	/**
	 * 是否有下一页
	 */
	boolean next = false;
	/**
	 * 是否有前一页
	 */
	boolean previous = false;

	public Pagination(int rows, int pageSize) {
		this.init(rows, pageSize);
	}

	public Pagination() {

	}

	/**
	 * 初始化分页参数:需要先设置totalRows
	 */
	public void init(int rows, int pageSize) {

		this.pageSize = pageSize;

		this.totalHit = rows;

		if ((totalHit % pageSize) == 0) {
			totalPages = totalHit / pageSize;
		} else {
			totalPages = totalHit / pageSize + 1;
		}

	}

	@Override
	public void init(int rows, int pageSize, int currentPage) {

		this.pageSize = pageSize;

		this.totalHit = rows;

		if ((totalHit % pageSize) == 0) {
			totalPages = totalHit / pageSize;
		} else {
			totalPages = totalHit / pageSize + 1;
		}
		if (currentPage != 0)
			gotoPage(currentPage);
	}

	/**
	 * 计算当前页的取值范围：pageStartRow和pageEndRow
	 */
	private void calculatePage() {
		previous = (pageNo - 1) > 0;

		next = pageNo < totalPages;

		if (pageNo * pageSize < totalHit) { // 判断是否为最后一页
			pageEndRow = pageNo * pageSize;
			pageStartRow = pageEndRow - pageSize;
		} else {
			pageEndRow = totalHit;
			pageStartRow = pageSize * (totalPages - 1);
		}

	}

	/**
	 * 直接跳转到指定页数的页面
	 * 
	 * @param page
	 */
	public void gotoPage(int page) {

		pageNo = page;

		calculatePage();

	}

	public String debugString() {

		return "共有数据数:" + totalHit + "共有页数:" + totalPages + "当前页数为:"
				+ pageNo + "是否有前一页:" + previous + "是否有下一页:" + next
				+ "开始行数:" + pageStartRow + "终止行数:" + pageEndRow;

	}

	@Override
	public int getPageNo() {
		return pageNo;
	}

	@Override
	public boolean isNext() {
		return next;
	}

	@Override
	public boolean isPrevious() {
		return previous;
	}

	@Override
	public int getPageEndRow() {
		return pageEndRow;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public int getPageStartRow() {
		return pageStartRow;
	}

	@Override
	public int getTotalPages() {
		return totalPages;
	}

	@Override
	public int getTotalHit() {
		return totalHit;
	}

	@Override
	public void setTotalPages(int i) {
		totalPages = i;
	}

	@Override
	public void setPageNo(int i) {
		pageNo = i;
	}

	@Override
	public void setNext(boolean b) {
		next = b;
	}

	@Override
	public void setPrevious(boolean b) {
		previous = b;
	}

	@Override
	public void setPageEndRow(int i) {
		pageEndRow = i;
	}

	@Override
	public void setPageSize(int i) {
		pageSize = i;
	}

	@Override
	public void setPageStartRow(int i) {
		pageStartRow = i;
	}

	@Override
	public void setTotalHit(int i) {
		totalHit = i;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pagination [pageSize=");
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
		builder.append(next);
		builder.append(", previous=");
		builder.append(previous);
		builder.append("]");
		return builder.toString();
	}
}
