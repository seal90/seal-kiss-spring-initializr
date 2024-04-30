package io.github.seal90.kiss.client.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

public class PageRequest {


    /** 页码 从1开始 */
    @NotNull
    @Min(1)
    private Integer pageNum;

    /** 页大小 */
    @NotNull
    @Min(0)
    private Integer pageSize;

    /** 排序 */
    private List<OrderBy> orderBy;

    /**
     * 排序
     */
    @Data
    public static class OrderBy {

        /** 排序属性 */
        private String key;

        /** 排序方式 */
        private Order order;
    }

    /**
     * 排序顺序
     */
    public static enum Order {

        /** 升序 */
        ASC,

        /** 降序 */
        DESC;
    }
}
