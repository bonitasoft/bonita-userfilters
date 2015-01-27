package org.bonitasoft.userfilter.test;

import org.bonitasoft.engine.TestsInitializer;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner.Initializer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(BonitaSuiteRunner.class)
@Initializer(TestsInitializer.class)
@SuiteClasses({
        SingleUserFilterTest.class,
        UserManagerFilterTest.class,
        ProcessInitiatorUserFilterTest.class,
        ProcessinitiatorManagerUserFilterTest.class,
        CustomUserInfoUserFilterIT.class,
        SameTaskUserFilterTest.class
})
public class UserFilterTest {

}
