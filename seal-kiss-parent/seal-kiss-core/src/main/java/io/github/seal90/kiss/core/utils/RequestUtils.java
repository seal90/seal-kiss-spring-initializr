package io.github.seal90.kiss.core.utils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtils {

    public static final String REQUEST_HANDLE_DURATION = "REQUEST_HANDLE_DURATION_ATTRIBUTE";

    public static HttpServletRequest request(){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)attributes).getRequest();
        return request;
    }

    public static String uri(){
        HttpServletRequest request = request();
        return request.getRequestURI();
    }

    public static void putStartHandleTime(){
        HttpServletRequest request = request();
        request.setAttribute(REQUEST_HANDLE_DURATION, System.currentTimeMillis());
    }

    public static long realTime(){
        HttpServletRequest request = request();
        Object startTime = request.getAttribute(REQUEST_HANDLE_DURATION);
        if(null == startTime){
            return -1L;
        }
        return System.currentTimeMillis() - Long.parseLong(startTime.toString());
    }

    public static String uri(ServletRequest servletRequest){
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        return request.getRequestURI();
    }

    public static void putStartHandleTime(ServletRequest servletRequest){
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        request.setAttribute(REQUEST_HANDLE_DURATION, System.currentTimeMillis());
    }

    public static long realTime(ServletRequest servletRequest){
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        Object startTime = request.getAttribute(REQUEST_HANDLE_DURATION);
        if(null == startTime){
            return -1L;
        }
        return System.currentTimeMillis() - Long.parseLong(startTime.toString());
    }
}
