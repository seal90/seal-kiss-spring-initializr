package io.github.seal90.kiss.client.result;

import java.util.List;

/**
 * 分页信息
 * @param <T>
 */
public class Page<T> {

    /** 页码 从1开始 */
    private Integer pageNum;

    /** 页大小 */
    private Integer pageSize;

    /** 总记录数 */
    private Long totalNum;

    /** 当前页数据 */
    private List<T> data;
}
