package org.autumn.mybatis.mapperhandler.page;

import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Pageable;

/**
 * Copy Right Information : @Copyright@ <br>
 * Project : @Project@ <br>
 * Description : 分页适配<br>
 * Author : andyslin <br>
 * Version : 0.0.1 <br>
 * Date : 2018-12-24<br>
 */
public class PageableRowBounds extends RowBounds {

    private final Pageable pageable;

    private long total;

    private PageableRowBounds(Pageable pageable, long total) {
        this.pageable = pageable;
        this.total = total;
    }

    public static PageableRowBounds build(Pageable pageable) {
        return build(pageable, -1);
    }

    public static PageableRowBounds build(Pageable pageable, long total) {
        return new PageableRowBounds(pageable, total);
    }

    public boolean isPaged() {
        return pageable.isPaged();
    }

    public Pageable getPageable() {
        return pageable;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public int getOffset() {
        return 0;
    }
}
