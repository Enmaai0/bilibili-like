package com.bilibili.domain;

import java.util.List;

public class PageResult<T> {
    private int total;

    private List<T> items;

    public PageResult(List<T> items, int total) {
        this.items = items;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
