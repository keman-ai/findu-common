package com.findu.common.dto;

/**
 * 分页信息传输对象，描述分页查询的页码、大小与总记录数。
 */
public class PaginationDTO {

    /**
     * 当前页码，从 1 开始。
     */
    private int page;

    /**
     * 每页记录数。
     */
    private int pageSize;

    /**
     * 满足条件的总记录数。
     */
    private long total;

    public PaginationDTO() {
    }

    public PaginationDTO(int page, int pageSize, long total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}

