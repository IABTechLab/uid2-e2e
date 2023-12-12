package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.operator.V2ApiOperatorLocalOnlyTest;
import suite.operator.V2ApiOperatorTest;

@Suite
@SelectClasses({
        BasicTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorLocalOnlyTest.class
})
public class E2EPipelinePrivateOperatorTestSuite {
}
