package com.study.spring.aop.advice;

import java.lang.reflect.Method;

/**
 * 异常通知
 */
public interface ThrowsAdvice extends Advice {

    void afterThrowing(Method method, Object[] args, Object target, Exception ex) throws Throwable;
}
