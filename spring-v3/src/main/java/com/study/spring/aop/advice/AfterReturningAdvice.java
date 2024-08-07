package com.study.spring.aop.advice;

import java.lang.reflect.Method;

/**
 * 后置通知
 */
public interface AfterReturningAdvice extends Advice {
	/**
	 * 实现该方法，提供AfterReturn增强
	 * 
	 * @param returnValue
	 *            返回值
	 * @param method
	 *            被增强的方法
	 * @param args
	 *            方法的参数
	 * @param target
	 *            方法的所属对象
	 * @throws Throwable
	 */
	void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
