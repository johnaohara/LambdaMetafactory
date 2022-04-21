package org.jboss.perf.scratch;

import org.jboss.perf.model.Company;
import org.jboss.perf.model.Person;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import java.util.function.Function;

public class DeleteMe {
    public static void main(String[] args) throws Throwable {

        Function<Person, String> personNameFunction = (Function<Person, String>) DeleteMe.buildGetterLambda(String.class, Person.class, "getName");
        Function<Person, Integer> personAgeFunction = (Function<Person, Integer>) DeleteMe.buildGetterLambda(Integer.class, Person.class,  "getAge");
        Function<Company, String> companyGetterFunction = (Function<Company, String>) DeleteMe.buildGetterLambda(String.class, Company.class, "getName");

        Consumer<Person> personPrintNameFunction = (Consumer<Person>) DeleteMe.buildConsumerLambda(Person.class,  "printName");
        Consumer<Person> personPrintNameGenericConsumer = DeleteMe.buildGenericFunctionalLambda(Consumer.class, Person.class,  "printName");

        Person ann = new Person("Ann", 21);

        System.out.println(personNameFunction.apply(ann));
        System.out.println(personAgeFunction.apply(ann));
        System.out.println(companyGetterFunction.apply(new Company("Red Hat")));

        personPrintNameFunction.accept(ann);
        personPrintNameGenericConsumer.accept(ann);

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


}