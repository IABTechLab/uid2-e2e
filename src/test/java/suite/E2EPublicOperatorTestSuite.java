package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BasicTestSuite.class,
        OperatorTestSuite.class,
        OptoutTestSuite.class
})
public class E2EPublicOperatorTestSuite {
}
