package com.study.spring.aop.pointcut;

import java.lang.reflect.Method;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

/**
 * aspectj 依赖
 */
public class AspectJExpressionPointcut implements Pointcut {

	private static PointcutParser pp = PointcutParser
			.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();

	private String expression;

	private PointcutExpression pe;

	/**
	 * 灵活地指定多个方法点,切点表达式
	 * @param expression 表达式
	 */
	public AspectJExpressionPointcut(String expression) {
		super();
		this.expression = expression;
		//解析表达式
		pe = pp.parsePointcutExpression(expression);
	}

	@Override
	public boolean matchsClass(Class<?> targetClass) {
		return pe.couldMatchJoinPointsInType(targetClass);
	}

	@Override
	public boolean matchsMethod(Method method, Class<?> targetClass) {
		ShadowMatch sm = pe.matchesMethodExecution(method);
		return sm.alwaysMatches();
	}

	public String getExpression() {
		return expression;
	}

}
