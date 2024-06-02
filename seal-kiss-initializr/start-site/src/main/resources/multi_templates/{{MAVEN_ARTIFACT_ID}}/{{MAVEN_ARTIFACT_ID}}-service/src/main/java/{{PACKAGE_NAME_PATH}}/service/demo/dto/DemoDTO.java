package {{MAVEN_PACKAGE_NAME}}.service.demo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DemoDTO {
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
    private Duration durationAttr;
}
