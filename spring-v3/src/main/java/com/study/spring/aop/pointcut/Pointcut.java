package com.study.spring.aop.pointcut;

import java.lang.reflect.Method;

public interface Pointcut {

	/**
	 * 找类
	 * @param targetClass
	 * @return
	 */
	boolean matchsClass(Class<?> targetClass);

	/**
	 * 找方法
	 * @param method
	 * @param targetClass
	 * @return
	 */
	boolean matchsMethod(Method method, Class<?> targetClass);
}
