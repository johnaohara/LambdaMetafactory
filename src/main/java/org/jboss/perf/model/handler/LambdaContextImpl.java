package org.jboss.perf.model.handler;

import java.lang.invoke.*;
import java.util.function.Consumer;

public class LambdaContextImpl extends AbstractContext{

    private final Consumer[] handlerConsumers;

    public LambdaContextImpl(RestHandler[] handlerChain) {
        super(handlerChain);
        handlerConsumers = new Consumer[this.handlerChain.length];
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (int pos = 0; pos < this.handlerChain.length; pos++) {
            try {
                MethodHandle virtMethodHandle = lookup.findVirtual(this.handlerChain[pos].getClass(), "handle",
                        MethodType.methodType(void.class, AbstractContext.class));
                //                MethodHandle virtMethodHandle = lookup.findVirtual(this.handlers[pos].getClass(), "handle",
                //                        MethodType.methodType(void.class));

                CallSite site = LambdaMetafactory.metafactory(
                        lookup,
                        "accept",
                        MethodType.methodType(Consumer.class),
                        MethodType.methodType(void.class, Object.class),
                        virtMethodHandle,
                        MethodType.methodType(void.class, this.handlerChain[pos].getClass()));
                handlerConsumers[pos] = (Consumer) site.getTarget().invokeExact();
            } catch (Throwable e) {
                System.out.println("Exception occurred. can not create lambda, will fall back to slow path for this handler type!");

                handlerConsumers[pos] = null;
            }
        }
    }

    @Override
    public void handleSomething() throws Exception {
        for( int chainPos = 0 ; chainPos < handlerChain.length ; chainPos++ ){
            if (handlerConsumers[chainPos] != null) {
                handlerConsumers[chainPos].accept(this);
            } else { //fallback to slow path
                handlerChain[chainPos].handle(this);
            }
        }

    }
}
