package org.jboss.perf.model.handler;

public class RoutingHandler implements RestHandler{

    private static final String name = RoutingHandler.class.getName();

    @Override
    public void handle(AbstractContext requestContext) throws Exception {
        System.out.println("handle: RoutingHandler");
        requestContext.incCount();
    }
}
