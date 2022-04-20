package org.jboss.perf.scratch;

import org.jboss.perf.model.Company;
import org.jboss.perf.model.Person;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

public class DeleteMe {
    public static void main(String[] args) throws Throwable {

        Function<Person, String> personNameFunction = (Function<Person, String>) DeleteMe.buildGetterLambda(String.class, Person.class, Function.class, "getName");
        Function<Person, Integer> personAgeFunction = (Function<Person, Integer>) DeleteMe.buildGetterLambda(Integer.class, Person.class, Function.class, "getAge");
        Function<Company, String> companyGetterFunction = (Function<Company, String>) DeleteMe.buildGetterLambda(String.class, Company.class, Function.class,"getName");

        Person ann = new Person("Ann", 21);

        System.out.println(personNameFunction.apply(ann));
        System.out.println(personAgeFunction.apply(ann));
        System.out.println(companyGetterFunction.apply(new Company("Red Hat")));

    }

    static <T, R> Object buildGetterLambda(Class<R> returnType, Class<T> objType, Class<Function> interfaceClass, String gettername) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
            "apply",
            MethodType.methodType(interfaceClass),
            MethodType.methodType(Object.class, Object.class),
            lookup.findVirtual(objType, gettername, MethodType.methodType(returnType)),
            MethodType.methodType(returnType, objType));

//        return (K) site.getTarget().invokeExact();
        return (Function) site.getTarget().invokeExact();

    }

}