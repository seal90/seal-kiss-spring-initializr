package com.github.seal90.kiss.multi.integration.db.dos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;

@Data
public class DemoDO implements Serializable {
    /** 
     * 主键 
     */
    private Long id;

    /** 
     * 字符串测试 
     */
    private String stringAttr;

    /** 
     * 整型测试 
     */
    private Integer integerAttr;

    /** 
     * 大整型测试 
     */
    private Long longAttr;

    /** 
     * 高精度测试 
     */
    private BigDecimal bigDecimalAttr;

    /** 
     * 时间测试 
     */
    private LocalTime localTimeAttr;

    /** 
     * 日期测试 
     */
    private LocalDate localDateAttr;

    /** 
     * 日期时间测试 
     */
    private LocalDateTime localDateTimeAttr;

    /** 
     * 时长测试 
     */
    private Long durationAttr;

    private static final long serialVersionUID = 1L;
}