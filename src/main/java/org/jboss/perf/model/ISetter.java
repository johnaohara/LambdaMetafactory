package org.jboss.perf.model;

@FunctionalInterface
public interface ISetter<T, R> {
    void set(T object, R value);
}
