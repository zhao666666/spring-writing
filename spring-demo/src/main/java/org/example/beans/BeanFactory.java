package org.example.beans;

/**
 * @author ZhaoJie
 * @date 2024/7/16 10:29
 */
public interface BeanFactory {
    /**
     * 获取bean
     * @param name
     * @return
     * @throws Exception
     */
    Object getBean(String name) throws Exception;
}
