package org.jboss.perf.model;

@FunctionalInterface
public interface Invoker<T, R> {
    void invoke(T object, R value);
}
