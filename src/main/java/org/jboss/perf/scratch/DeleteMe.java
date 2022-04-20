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

        Function<Person, String> personGetterFunction = (Function<Person, String>) DeleteMe.<Function, Person, String>buildGetterLambda(String.class, Person.class, Function.class, "getName");
        Function<Company, String> companyGetterFunction = (Function<Company, String>) buildGetterLambda(String.class, Company.class, Function.class,"getName");

        System.out.println(personGetterFunction.apply(new Person("Ann")));
        System.out.println(companyGetterFunction.apply(new Company("Red Hat")));

    }

    static <K, T, R> Object buildGetterLambda(Class<R> returnType, Class<T> objType, Class<K> interfaceClass, String gettername) throws Throwable{

        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        final CallSite site = LambdaMetafactory.metafactory(lookup,
            "apply",
            MethodType.methodType(interfaceClass),
            MethodType.methodType(Object.class, Object.class),
            lookup.findVirtual(objType, gettername, MethodType.methodType(returnType)),
            MethodType.methodType(returnType, objType));

        return (K) site.getTarget().invokeExact();

    }

}