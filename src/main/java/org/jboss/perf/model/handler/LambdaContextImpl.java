package org.jboss.perf.model.handler;

import org.jboss.perf.model.Invoker;
import org.jboss.perf.scratch.MetafactoryUtils;

import java.lang.invoke.*;

public class LambdaContextImpl extends AbstractContext{

    private final Invoker<RestHandler, AbstractContext>[] handlerConsumers;

    public LambdaContextImpl(RestHandler[] handlerChain) {
        super(handlerChain);
        handlerConsumers = new Invoker[this.handlerChain.length];
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (int pos = 0; pos < this.handlerChain.length; pos++) {
            try {

                Invoker<RestHandler, AbstractContext> restInvoker = MetafactoryUtils.getInvoker(this.handlerChain[pos].getClass(), "handle", AbstractContext.class);
                handlerConsumers[pos] = restInvoker;
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
                handlerConsumers[chainPos].invoke(handlerChain[chainPos],this);
            } else { //fallback to slow path
                handlerChain[chainPos].handle(this);
            }
        }

    }
}
