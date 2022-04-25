/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.jboss.perf.model.Company;
import org.jboss.perf.model.ISetter;
import org.jboss.perf.model.Invoker;
import org.jboss.perf.model.Person;
import org.jboss.perf.model.handler.*;
import org.jboss.perf.scratch.MetafactoryUtils;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class LibraryTest {
    @Test public void testlambdaMetaFactory() throws Throwable {

        Function<Person, String> personNameFunction = (Function<Person, String>) MetafactoryUtils.buildGetterLambda(String.class, Person.class, "getName");
        Function<Person, Integer> personAgeFunction = (Function<Person, Integer>) MetafactoryUtils.buildGetterLambda(Integer.class, Person.class,  "getAge");
        Function<Company, String> companyGetterFunction = (Function<Company, String>) MetafactoryUtils.buildGetterLambda(String.class, Company.class, "getName");

        Consumer<Person> personPrintNameFunction = (Consumer<Person>) MetafactoryUtils.buildConsumerLambda(Person.class,  "printName");
//        Consumer<Person> personPrintNameGenericConsumer = DeleteMe.buildGenericFunctionalLambda(Consumer.class, Person.class,  "printName");

        Person ann = new Person("Ann", 21);

        System.out.println(personNameFunction.apply(ann));
        System.out.println(personAgeFunction.apply(ann));
        System.out.println(companyGetterFunction.apply(new Company("Red Hat")));


        ISetter<Person, String> personNameSetter = MetafactoryUtils.getSetter(Person.class, "name", String.class);
        personNameSetter.set(ann, "Jane");

        Invoker<RandomHandler, AbstractContext> randomInvoker = MetafactoryUtils.getInvoker(RandomHandler.class, "handle", AbstractContext.class);
        Invoker<RestHandler, AbstractContext> restInvoker = MetafactoryUtils.getInvoker(ExceptionHandler.class, "handle", AbstractContext.class);


        personPrintNameFunction.accept(ann);
//        personPrintNameGenericConsumer.accept(ann);

        RestHandler[] smallHandlerChain = {new RandomHandler(), new ExceptionHandler(), new RoutingHandler()};

        LambdaContextImpl lambdaContext = new LambdaContextImpl(smallHandlerChain);

        lambdaContext.handleSomething();

        System.out.println(lambdaContext.getCount());

        randomInvoker.invoke((RandomHandler) smallHandlerChain[0], lambdaContext);

        restInvoker.invoke(smallHandlerChain[1], lambdaContext);

        System.out.println(lambdaContext.getCount());


//        lambdaContext.handleSomething();

//        System.out.println(lambdaContext.getCount());

    }

}
