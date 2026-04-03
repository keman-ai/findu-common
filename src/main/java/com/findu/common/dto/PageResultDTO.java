package com.findu.common.dto;

import java.util.List;

/**
 * 通用分页结果包装，包含数据列表与分页信息。
 *
 * @param <T> 数据类型
 */
public class PageResultDTO<T> {

    /**
     * 数据记录列表。
     */
    private List<T> items;

    /**
     * 分页数据。
     */
    private PaginationDTO pagination;

    public PageResultDTO() {
    }

    public PageResultDTO(List<T> items, PaginationDTO pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public PaginationDTO getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDTO pagination) {
        this.pagination = pagination;
    }
}

