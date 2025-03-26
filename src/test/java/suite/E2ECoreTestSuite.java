package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class
})
public class E2ECoreTestSuite {
}
