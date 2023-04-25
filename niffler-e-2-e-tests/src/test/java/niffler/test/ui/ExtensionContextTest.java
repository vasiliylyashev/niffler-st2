package niffler.test.ui;

import niffler.jupiter.extension.ExtensionContextLifecycleExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ExtensionContextLifecycleExtension.class)
public class ExtensionContextTest {

    @AfterAll
    public static void afterAll() {
        System.out.println("--->>> @AfterAll");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("  --->>> @afterEach");
    }

    @BeforeAll
    public static void beforeAll() {
        System.out.println("--->>> @beforeAll");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("   --->>> @beforeEach");
    }

    @Test
    void test1() {
        System.out.println("      --->>> @Test test1()");
    }

//    @Test
//    void test2() {
//        System.out.println("      --->>> @Test test2()");
//    }
//
//    @Test
//    void test3() {
//        System.out.println("      --->>> @Test test3()");
//    }

}
