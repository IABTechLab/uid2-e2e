package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.operator.V2ApiOperatorTest;

@Suite
@SelectClasses({
        BasicTestSuite.class,
        V2ApiOperatorTest.class,
        OptoutTestSuite.class
})
public class E2EPrivateOperatorTestSuite {
}
