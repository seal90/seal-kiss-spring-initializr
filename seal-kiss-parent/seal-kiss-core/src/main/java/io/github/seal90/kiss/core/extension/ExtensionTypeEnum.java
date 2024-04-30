package io.github.seal90.kiss.core.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扩展点类型枚举
 */
@Getter
@AllArgsConstructor
public enum ExtensionTypeEnum {

    /** 校验器 工作在controller层 */
    VALIDATOR("VALIDATOR", "校验器"),

    /** 业务处理器 工作在业务服务层 */
    PROCESSOR("PROCESSOR", "处理器"),

    /** 提供服务的原子节点 工作在core层 */
    NODE("NODE", "提供服务的原子节点"),
    ;

    /** 枚举Code编码 */
    private String code;

    /** 枚举描述 */
    private String desc;

}