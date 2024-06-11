package io.github.seal90.kiss.gateway.support;


public class SealServerWebExchangeUtils {
    
    public static final String GATEWAY_ALREADY_GRAY_PATH_ATTR = qualify("gatewayAlreadyGrayPath");

    
    private static String qualify(String attr) {
		return SealServerWebExchangeUtils.class.getName() + "." + attr;
	}
}
