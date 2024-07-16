package org.example.beans;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ZhaoJie
 * @date 2024/7/16 10:31
 */
public interface BeanDefinition {
    /**
     * 单例
     */
    String SCOPE_SINGLETION = "singleton";

    /**
     * 原型
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 类
     */
    Class<?> getBeanClass();

    /**
     * Scope
     */
    String getScope();

    /**
     * 是否单例
     */
    boolean isSingleton();

    /**
     * 是否原型
     */
    boolean isPrototype();

    /**
     * 工厂bean名
     */
    String getFactoryBeanName();

    /**
     * 工厂方法名
     */
    String getFactoryMethodName();

    /**
     * 初始化方法
     */
    String getInitMethodName();

    /**
     * 销毁方法
     */
    String getDestroyMethodName();

    boolean isPrimary();

    /**
     * 校验bean定义的合法性
     * 1.构成方法  类名
     * 2.工厂静态   工厂类名+工厂方法名
     * 3.工厂方法   工厂bean名+工厂方法名
     */
    default boolean validate() {
        // 没定义class,工厂bean或工厂方法没指定，则不合法。三种创建实例的方法
        if (this.getBeanClass() == null) {
            if (StringUtils.isBlank(getFactoryBeanName()) || StringUtils.isBlank(getFactoryMethodName())) {
                return false;
            }
        }

        // 定义了类，又定义工厂bean，不合法
        if (this.getBeanClass() != null && StringUtils.isNotBlank(getFactoryBeanName())) {
            return false;
        }

        return true;
    }

}
