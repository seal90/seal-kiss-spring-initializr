package io.github.seal90.kiss.core.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 配置日志打印切面
 */
@Aspect
public class MonitorLogAspect {

    @Autowired
    private MonitorLogTemplate monitorLogTemplate;

    @Pointcut("@annotation(io.github.seal90.kiss.core.log.MonitorLog)")
    public void log(){}

    /**
     * 日志输出
     * @param pjp
     * @param monitorLog
     * @return
     * @throws Throwable
     */
    @Around("log() && @annotation(monitorLog)")
    public Object log(ProceedingJoinPoint pjp, MonitorLog monitorLog) throws Throwable {
//        String clazzName = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().toShortString();

        // 获取执行参数
        Object[] args = pjp.getArgs();
        // 执行方法
        // 输出日志
        return monitorLogTemplate.logWithException(monitorLog.enableMetrics(),
                monitorLog.enableArgs(), monitorLog.enablePrintStackTrace(),
                monitorLog.origin(), methodName, (innerArgs) -> {
                    Object val = pjp.proceed();
                    return val;
                }, args);

    }

}
