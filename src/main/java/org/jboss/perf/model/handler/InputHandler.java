package org.jboss.perf.model.handler;


public class InputHandler implements RestHandler{

    private static final String name = InputHandler.class.getName();

    @Override
    public void handle(AbstractContext requestContext) throws Exception {
        System.out.println("handle: InputHandler");
        requestContext.incCount();
    }
}
