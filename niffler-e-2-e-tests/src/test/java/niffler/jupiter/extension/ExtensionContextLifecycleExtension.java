package niffler.jupiter.extension;

import org.junit.jupiter.api.extension.*;

public  class ExtensionContextLifecycleExtension implements  AfterEachCallback, AfterAllCallback, AfterTestExecutionCallback,
        BeforeEachCallback, BeforeAllCallback, BeforeTestExecutionCallback  {
    @Override
    public void afterAll(ExtensionContext context)  {
        System.out.println("afterAll");
        contextPrint(context);
    }

    @Override
    public void afterEach(ExtensionContext context)  {
        System.out.println("afterEach");
        contextPrint(context);
    }

    @Override
    public void afterTestExecution(ExtensionContext context)  {
        System.out.println("afterTestExecution");
        contextPrint(context);
    }

    @Override
    public void beforeAll(ExtensionContext context)  {
        System.out.println("beforeAll");
        contextPrint(context);
    }

    @Override
    public void beforeEach(ExtensionContext context)  {
        System.out.println("beforeEach");
        contextPrint(context);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context)  {
        System.out.println("beforeTestExecution");
        contextPrint(context);
    }

    private void contextPrint(ExtensionContext context){
        try{
            context.getRequiredTestMethod();
            System.out.println("context.getRequiredTestMethod();");
        }catch (Throwable e){};
        try{
            context.getRequiredTestClass();
            System.out.println("context.getRequiredTestClass();");
        }catch (Throwable e){};
        try{
            context.getRequiredTestInstance();
            System.out.println(" context.getRequiredTestInstance();");
        }catch (Throwable e){};
    }
}
