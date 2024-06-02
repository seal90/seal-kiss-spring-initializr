package {{MAVEN_PACKAGE_NAME}}.common.enums;

import io.github.seal90.kiss.base.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizErrorReason implements ErrorReason {

    TEST("TEST", "测试"),
    ;
    private String code;

    private String msg;
}
