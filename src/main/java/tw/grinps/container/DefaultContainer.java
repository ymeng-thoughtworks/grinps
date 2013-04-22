package tw.grinps.container;

import tw.grinps.BeanContainer;
import tw.grinps.NotMatchedInterfaceException;

import java.util.HashMap;
import java.util.Map;

public class DefaultContainer implements BeanContainer {

    private Map<Class<?>, Object> instancePool = new HashMap<Class<?>, Object>();

    @Override
    public DefaultContainer registerBean(Class<?> interfaceType, Class<?> instanceType) {
        if (!isDeclaredInterface(instanceType, interfaceType)) {
            throw new NotMatchedInterfaceException();
        }

        InstanceGenerator instanceGenerator = new InstanceGenerator(this);
        Object instance = instanceGenerator.generate(instanceType);

        SetterInjector injector = new SetterInjector(this);
        injector.inject(instance);

        this.instancePool.put(interfaceType, instance);

        return this;
    }

    @Override
    public DefaultContainer registerBean(Class<?> instanceType) {
        return registerBean(instanceType, instanceType);
    }

    @Override
    public <T> T getSingletonBean(Class<T> interfaceType) {
        return (T) this.instancePool.get(interfaceType);
    }

    @Override
    public <T> T getNewBean(Class<T> interfaceType) {
        T singletonInstance = (T) this.instancePool.get(interfaceType);
        Class<?> instanceType = singletonInstance.getClass();

        InstanceGenerator instanceGenerator = new InstanceGenerator(this);
        return (T) instanceGenerator.generate(instanceType);
    }

    @Override
    public boolean hasBean(Class<?> interfaceType) {
        return this.instancePool.containsKey(interfaceType);
    }

    private static boolean isDeclaredInterface(Class<?> instanceType, Class<?> interfaceType) {
        return interfaceType.isAssignableFrom(instanceType);
    }

}
