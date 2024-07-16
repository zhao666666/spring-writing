package org.example.beans;

/**
 * bean定义注册
 * @author ZhaoJie
 * @date 2024/7/16 10:30
 */
public interface BeanDefinitionRegistry {
    /**
     * 通过beanName判断是否存在bean定义
     * @param beanName
     * @return
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 通过beanName获取bean定义
     * @param beanName
     * @return
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 注册bean定义
     * @param beanName
     * @param beanDefinition
     * @throws BeanDefinitionRegistException
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistException;
}
