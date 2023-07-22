package com.comphenix.tinyprotocol;

import java.lang.reflect.Constructor;

public class ConstructorInvokerHelper {
    private final Constructor<?> constructor;

    public ConstructorInvokerHelper(String className, Class<?>... params) {
        this.constructor = Reflection.getConstructor(Reflection.getClass(className), params);
    }

    public Object invoke(Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke constructor " + constructor, e);
        }
    }
}
