package org.jboss.perf.scratch;

import org.jboss.perf.model.Company;
import org.jboss.perf.model.ISetter;
import org.jboss.perf.model.Invoker;
import org.jboss.perf.model.Person;
import org.jboss.perf.model.handler.*;

import java.lang.invoke.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class DeleteMe {
    public static void main(String[] args) throws Throwable {

        Function<Person, String> personNameFunction = (Function<Person, String>) DeleteMe.buildGetterLambda(String.class, Person.class, "getName");
        Function<Person, Integer> personAgeFunction = (Function<Person, Integer>) DeleteMe.buildGetterLambda(Integer.class, Person.class,  "getAge");
        Function<Company, String> companyGetterFunction = (Function<Company, String>) DeleteMe.buildGetterLambda(String.class, Company.class, "getName");

        Consumer<Person> personPrintNameFunction = (Consumer<Person>) DeleteMe.buildConsumerLambda(Person.class,  "printName");
//        Consumer<Person> personPrintNameGenericConsumer = DeleteMe.buildGenericFunctionalLambda(Consumer.class, Person.class,  "printName");

        Person ann = new Person("Ann", 21);

        System.out.println(personNameFunction.apply(ann));
        System.out.println(personAgeFunction.apply(ann));
        System.out.println(companyGetterFunction.apply(new Company("Red Hat")));


        ISetter<Person, String> personNameSetter = getSetter(Person.class, "name", String.class);
        personNameSetter.set(ann, "Jane");

        Invoker<RandomHandler, AbstractContext> randomInvoker = getInvoker(RandomHandler.class, "handle", AbstractContext.class);
        Invoker<RestHandler, AbstractContext> restInvoker = getInvoker(ExceptionHandler.class, "handle", AbstractContext.class);


        personPrintNameFunction.accept(ann);
//        personPrintNameGenericConsumer.accept(ann);

        RestHandler[] smallHandlerChain = {new RandomHandler(), new ExceptionHandler(), new RoutingHandler()};

        LambdaContextImpl lambdaContext = new LambdaContextImpl(smallHandlerChain);

        randomInvoker.invoke((RandomHandler) smallHandlerChain[0], lambdaContext);

        restInvoker.invoke(smallHandlerChain[1], lambdaContext);


//        lambdaContext.handleSomething();

//        System.out.println(lambdaContext.getCount());

    }

    static <T, R> Object buildGetterLambda(Class<R> returnType, Class<T> objType, String gettername) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
            "apply",
            MethodType.methodType(Function.class),
            MethodType.methodType(Object.class, Object.class),
            lookup.findVirtual(objType, gettername, MethodType.methodType(returnType)),
            MethodType.methodType(returnType, objType));

        return (Function) site.getTarget().invokeExact();

    }

    static <T, R> Object buildSetterLambda(Class<R> paramType, Class<T> objType, String setterName) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
            "apply",
            MethodType.methodType(BiConsumer.class),
            MethodType.methodType(Object.class, Object.class),
            lookup.findVirtual(objType, setterName, MethodType.methodType(Object.class)),
            MethodType.methodType(void.class, paramType.getClass()));

        return (Function) site.getTarget().invokeExact();

    }


    static <T> Object buildConsumerLambda(Class<T> objType, String methodName) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
            "accept",
            MethodType.methodType(Consumer.class),
            MethodType.methodType(void.class, Object.class),
            lookup.findVirtual(objType, methodName, MethodType.methodType(void.class)),
            MethodType.methodType(void.class, objType));

        Consumer returnObj = (Consumer) site.getTarget().invokeExact();
        return returnObj;

    }


    static <R, T> R buildGenericFunctionalLambda(Class<R> returnType, Class<T> objType, String methodName) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
                "accept",
                MethodType.methodType(returnType),
                MethodType.methodType(void.class, Object.class),
                lookup.findVirtual(objType, methodName, MethodType.methodType(void.class)),
                MethodType.methodType(void.class, objType));
        var returnObj = ((R) site.getTarget().invokeExact());
        return (R) returnObj;

    }


    public static <T extends RestHandler, R> Invoker<T, R> getInvoker(Class<? extends RestHandler> clazz, String fieldName,
                                                 Class<R> fieldType) throws Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType setter = MethodType.methodType(void.class, fieldType);
        MethodHandle target = caller.findVirtual(clazz, fieldName, setter);
        MethodType func = target.type();

        CallSite site = LambdaMetafactory.metafactory(
                caller,
                "invoke",
                MethodType.methodType(Invoker.class),
                func.erase(),
                target,
                func
        );

        MethodHandle factory = site.getTarget();
        Invoker<T, R> r = (Invoker<T, R>) factory.invokeExact();

        return r;
    }



    public static <T, R> ISetter<T, R> getSetter(Class<T> clazz, String fieldName,
                                                 Class<R> fieldType) throws Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType setter = MethodType.methodType(void.class, fieldType);
        MethodHandle target = caller.findVirtual(clazz, computeSetterName(fieldName), setter);
        MethodType func = target.type();

        CallSite site = LambdaMetafactory.metafactory(
                caller,
                "set",
                MethodType.methodType(ISetter.class),
                func.erase(),
                target,
                func
        );

        MethodHandle factory = site.getTarget();
        ISetter<T, R> r = (ISetter<T, R>) factory.invokeExact();

        return r;
    }

    public static String computeSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}