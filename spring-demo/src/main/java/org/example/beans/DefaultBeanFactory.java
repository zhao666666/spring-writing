package org.example.beans;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean创建工厂
 *  1. 实现Bean定义信息的注册
 *  2. 实现Bean工厂定义的getBean方法
 *  3. 实现初始化方法的执行
 *  4. 实现单例的要求
 *  5. 实现容器关闭是执行单例的销毁操作
 * @author ZhaoJie
 * @date 2024/7/16 10:30
 */
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {

    /**
     * bean定义信息,key是beanName
     */
    protected Map<String, BeanDefinition> beanDefintionMap = new ConcurrentHashMap<>(256);

    /**
     * 单例map
     */
    private final Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>(256);

    // 1. 实现Bean定义信息的注册
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistException {
        Objects.requireNonNull(beanName, "注册bean需要给入beanName");
        Objects.requireNonNull(beanDefinition, "注册bean需要给入beanDefinition");
        //校验合法性
        if(!beanDefinition.validate()){
            throw new BeanDefinitionRegistException(beanName+"bean定义是不合法的");
        }

        if(this.containsBeanDefinition(beanName)){
            throw new BeanDefinitionRegistException(beanName+"beanName已经存在");
        }

        beanDefintionMap.put(beanName,beanDefinition);
    }

    // 2. 实现Bean工厂定义的getBean方法
    @Override
    public Object getBean(String beanName) throws Exception {
        return this.doGetBean(beanName);
    }

    //4. 实现单例的要求
    private Object doGetBean(String beanName) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //判断是否合法
        Objects.requireNonNull(beanName, "beanName不能为空");

        //通过name 获取bean
        Object instance = singletonBeanMap.get(beanName);

        //存在单例
        if(instance != null){
            return instance;
        }

        BeanDefinition bd = this.getBeanDefinition(beanName);
        Objects.requireNonNull(bd,"beanDefinition不能为空");
        if(bd.isSingleton()){
            //双重检查判断是否存在,单例模式
            synchronized (this.singletonBeanMap){
                instance = singletonBeanMap.get(beanName);
                if(instance == null){
                    instance = doCreateInstance(bd);
                    this.singletonBeanMap.put(beanName,instance);
                }
            }
        }else{
            instance = doCreateInstance(bd);
        }
        return instance;
    }

    /**
     * 创建代理对象
     * @param bd
     * @return
     */
    private Object doCreateInstance(BeanDefinition bd) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<?> type = bd.getBeanClass();
        Object instance = null;
        //type不为空 1.构成方法 2.静态工厂类 + 工厂方法名
        if(type != null){
            //构造方法
            if(StringUtils.isBlank(bd.getFactoryMethodName())){
                instance = this.createInstanceByConstructor(bd);
            }else{
                //静态工程类,类
                instance = this.createInstanceByStaticFactoryMethod(bd);
            }
        }else{
            //工厂bean类 + 工厂方法名
            instance = this.createInstanceByFactoryBean(bd);
        }
        // 执行初始化方法
        this.doInit(bd, instance);

        return instance;
    }

    /**
     * 默认构造方法
     * @param bd
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object createInstanceByConstructor(BeanDefinition bd) throws InstantiationException, IllegalAccessException {
        return bd.getBeanClass().newInstance();
    }

    /**
     * 静态工厂, 工厂类+静态工厂方法
     * 是一个静态方法，可以通过类名直接调用，而无需创建该类的实例
     * @param bd
     * @return
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition bd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = bd.getBeanClass();
        Method method = type.getMethod(bd.getFactoryMethodName());
        return method.invoke(type);
    }

    /**
     * 工厂,需要获取工厂类的实例
     * 再通过该类的实例获取创建工厂方法名
     * @param bd
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private Object createInstanceByFactoryBean(BeanDefinition bd) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        //获取工厂类的实例
        Object factoryBean = doGetBean(bd.getFactoryBeanName());
        Method method = factoryBean.getClass().getMethod(bd.getFactoryMethodName());
        return method.invoke(factoryBean);
    }

    /**
     * 3. 实现初始化方法的执行
     * @param bd
     * @param instance
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private void doInit(BeanDefinition bd, Object instance) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        //执行init方法
        if(StringUtils.isNotBlank(bd.getInitMethodName())){
            Method method = instance.getClass().getMethod(bd.getInitMethodName());
            method.invoke(instance);
        }
    }

    @Override
    public void close() throws IOException {
        //调用销毁方法,只有单例需要销毁,原型不需要
        for (Map.Entry<String, BeanDefinition> entry : beanDefintionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition bd = entry.getValue();
            //单例 + 存在销毁方法
            if(bd.isSingleton() && StringUtils.isNotBlank(bd.getDestroyMethodName())){
                Object instance = this.singletonBeanMap.get(beanName);
                try {
                    Method m = instance.getClass().getMethod(bd.getDestroyMethodName());
                    m.invoke(instance);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                         | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //疑问：原型Bean如果指定了销毁方法，怎么办？原型不需要,通过jvm的生命周期销毁
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

}
