package io.github.seal90.kiss.gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties("seal.kiss.gray")
public class GrayConfigurationProperties {

    public static final String GRAY_CALC_HEADER = "X-GRAY-CALC";

    public static final String SEAL_GRAY_PATH_FLAG = "SEAL-GRAY-PATH";

    private String subSetEnvRequestKey = "SUB_SET_ENV";

    private Boolean enableClientRequestGrayHeader = true;

    private Map<String, GrayRule> routesRule;

    @Data
    public static final class GrayRule {

        private GrayModifyType modifyType;

        private GrayKeyFromType keyFromType;

        private GrayCalculateType calculateType;

        private List<String> inValues;

        private String targetGrayFlag;
    }

    public static enum GrayModifyType {
        GRAY_PATH,
        GRAY_ENV,
    }

    public static enum GrayKeyFromType {
        FROM_HEADER,
        // BY_USER,
    }

    public static enum GrayCalculateType {
        IN,
        // NUM_THOUSANDTH,
        // STR_HASH_THOUSANDTH,
    }
}
