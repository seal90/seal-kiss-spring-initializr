package io.github.seal90.kiss.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seal90.kiss.base.exception.SysErrorReason;
import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.core.exception.ServiceException;
import io.github.seal90.kiss.core.log.NotifyLog;
import io.github.seal90.kiss.core.log.SysNotifyLogScene;
import io.github.seal90.kiss.core.utils.RequestUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 全局兜底异常处理，包括 Filter 异常
 * 此处按照请求 content_type = APPLICATION_JSON 会处理异常
 */
@Slf4j
@Configuration
public class ExceptionGlobalConfig {

    @Bean
    public FilterRegistrationBean globalExceptionResolveFilterRegistration(ObjectMapper objectMapper) {
        FilterRegistrationBean registration = new FilterRegistrationBean(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                RequestUtils.putStartHandleTime(request);
                if(!MimeTypeUtils.APPLICATION_JSON.toString().equals(request.getContentType())) {
                    chain.doFilter(request, response);
                    return;
                }

                try {
                    chain.doFilter(request, response);
                }catch (Exception e){
                    log.warn("全局异常", e);
                    if(response.isCommitted()) {
                        log.error("全局异常处理捕获已经响应后产生的异常", e);
                        NotifyLog.log(SysNotifyLogScene.SYS_GLOBAL_EXCEPTION_COMMIT);

                    } else {
                        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                        httpServletResponse.reset();
                        httpServletResponse.setStatus(500);
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
                        Result<Void> result = Result.error(SysErrorReason.ERROR);
                        if(e instanceof ServiceException exception){
                            log.info("全局异常处理捕获业务异常", e);
                            result = Result.error(exception.getErrorReason());
                        } else {
                            log.error("全局异常处理捕获未处理异常", e);
                            NotifyLog.log(SysNotifyLogScene.SYS_GLOBAL_EXCEPTION_UNEXPECTED);
                        }
                        try (PrintWriter writer = httpServletResponse.getWriter()) {
                            String val = objectMapper.writeValueAsString(result);
                            writer.write(val);
                        }
                    }
                }
            }
        });
        registration.addUrlPatterns("/*");
        registration.setName("globalExceptionResolveFilter");
        registration.setOrder(Integer.MIN_VALUE +1 );
        return registration;
    }

}
