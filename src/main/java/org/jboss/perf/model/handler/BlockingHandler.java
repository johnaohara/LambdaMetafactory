package org.jboss.perf.model.handler;


public class BlockingHandler implements RestHandler{

    private static final String name = BlockingHandler.class.getName();

    @Override
    public void handle(AbstractContext requestContext) throws Exception {
        requestContext.incCount();
    }
}
