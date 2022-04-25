package org.jboss.perf.model.handler;

public interface RestHandler<T extends AbstractContext> {

    void handle(T requestContext) throws Exception;

}