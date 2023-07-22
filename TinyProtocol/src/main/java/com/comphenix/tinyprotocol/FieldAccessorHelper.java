package com.comphenix.tinyprotocol;

import java.lang.reflect.Field;

public class FieldAccessorHelper<T> implements Reflection.FieldAccessor<T> {
    private final Field field;

    public FieldAccessorHelper(Class<?> target, String name, Class<T> fieldType) {
        this.field = Reflection.getField(target, name, fieldType, 0);
    }

    @Override
    public T get(Object target) {
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access reflection.", e);
        }
    }

    @Override
    public void set(Object target, T value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access reflection.", e);
        }
    }

    @Override
    public boolean hasField(Object target) {
        return field.getDeclaringClass().isAssignableFrom(target.getClass());
    }
}
