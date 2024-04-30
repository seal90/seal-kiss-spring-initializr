package io.github.seal90.kiss.core.extension;

import java.lang.annotation.*;

/**
 * 扩展点注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Extension {

    /**
     * 扩展点类型
     * @see ExtensionTypeEnum
     */
    ExtensionTypeEnum type();

    /** 扩展点名称，建议多段式，防止重复，如XXX_XX_XX */
    String[] name() default "";

    /**
     * 业务动作，与name结合使用
     * name必填，action选填
     * 最终命名为 action + "_" + name + "_" + type
     */
    String action() default "";
}
