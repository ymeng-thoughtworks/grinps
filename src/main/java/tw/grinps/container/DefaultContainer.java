package tw.grinps.container;

import tw.grinps.BeanContainer;
import tw.grinps.NotMatchedInterfaceException;
import tw.grinps.ScopedContainer;

import java.util.HashMap;
import java.util.Map;

public class DefaultContainer implements BeanContainer, ScopedContainer {

    private Map<Class<?>, Object> instancePool = new HashMap<Class<?>, Object>();
    private DefaultContainer parent;

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
        if (hasBeanInCurrentScope(interfaceType)) {
            return getSingletonBeanFromCurrentScope(interfaceType);
        }

        if (this.parent != null) {
            return this.parent.getSingletonBean(interfaceType);
        }

        return null;
    }

    @Override
    public <T> T getNewBean(Class<T> interfaceType) {
        T singletonInstance = getSingletonBean(interfaceType);
        Class<?> instanceType = singletonInstance.getClass();

        InstanceGenerator instanceGenerator = new InstanceGenerator(this);
        return (T) instanceGenerator.generate(instanceType);
    }

    @Override
    public boolean hasBean(Class<?> interfaceType) {
        if (hasBeanInCurrentScope(interfaceType)) {
            return true;
        }

        if (this.parent != null) {
            return this.parent.hasBean(interfaceType);
        }

        return false;
    }

    @Override
    public void addChild(ScopedContainer container) {
        ((DefaultContainer) container).setParent(this);
    }

    private boolean hasBeanInCurrentScope(Class<?> interfaceType) {
        return this.instancePool.containsKey(interfaceType);
    }

    private <T> T getSingletonBeanFromCurrentScope(Class<T> interfaceType) {
        return (T) this.instancePool.get(interfaceType);
    }

    private static boolean isDeclaredInterface(Class<?> instanceType, Class<?> interfaceType) {
        return interfaceType.isAssignableFrom(instanceType);
    }

    public void setParent(DefaultContainer parent) {
        this.parent = parent;
    }
}
