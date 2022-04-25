package org.jboss.perf.model.handler;


public class ExceptionHandler implements RestHandler{

    private static final String name = ExceptionHandler.class.getName();

    @Override
    public void handle(AbstractContext requestContext) throws Exception {
        System.out.println("handle: ExceptionHandler");
        requestContext.incCount();
    }
}
