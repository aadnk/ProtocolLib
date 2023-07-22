package com.comphenix.tinyprotocol;

import java.lang.reflect.Method;

public class MethodInvokerHelper {
    private final Method method;

    public MethodInvokerHelper(String className, String methodName, Class<?>... params) {
        this.method = Reflection.getTypedMethod(Reflection.getClass(className), methodName, null, params);
    }

    public Object invoke(Object target, Object... arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke method " + method, e);
        }
    }
}
