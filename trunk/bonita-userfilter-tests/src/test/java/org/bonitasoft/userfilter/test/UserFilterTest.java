package org.bonitasoft.userfilter.test;

import javax.naming.Context;

import org.bonitasoft.engine.CommonAPITest;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.test.APITestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(Suite.class)
@SuiteClasses({
        SingleUserFilterTest.class,
        UserManagerFilterTest.class,
        ProcessInitiatorUserFilterTest.class,
        ProcessinitiatorManagerUserFilterTest.class,
        SameTaskUserFilterTest.class })
public class UserFilterTest {

    static ConfigurableApplicationContext springContext;

    @BeforeClass
    public static void beforeClass() throws BonitaException {
        System.err.println("=================== UserFilterTests.beforeClass()");

        setupSpringContext();

        APITestUtil.createPlatformStructure();
        CommonAPITest.beforeClass();
    }

    @AfterClass
    public static void afterClass() throws BonitaException, InterruptedException {
        System.err.println("=================== UserFilterTests.afterClass()");
        CommonAPITest.afterClass();
        APITestUtil.deletePlatformStructure();

        closeSpringContext();

    }

    private static void setupSpringContext() {
        setSystemPropertyIfNotSet("sysprop.bonita.db.vendor", "h2");

        // Force these system properties
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.bonitasoft.engine.local.SimpleMemoryContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.bonitasoft.engine.local");

        springContext = new ClassPathXmlApplicationContext("datasource.xml", "jndi-setup.xml");
    }

    private static void closeSpringContext() {
        springContext.close();
    }

    private static void setSystemPropertyIfNotSet(final String property, final String value) {
        System.setProperty(property, System.getProperty(property, value));
    }

}
