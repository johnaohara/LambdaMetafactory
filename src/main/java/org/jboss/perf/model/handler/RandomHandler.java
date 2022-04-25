package org.jboss.perf.model.handler;

public class RandomHandler implements RestHandler{

    private static final String name = RandomHandler.class.getName();

    @Override
    public void handle(AbstractContext requestContext) throws Exception {
        System.out.println("handle: RandomHandler");
        requestContext.incCount();
    }
}
