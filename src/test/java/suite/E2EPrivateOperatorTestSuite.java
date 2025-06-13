package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.operator.OperatorTest;

@Suite
@SelectClasses({
        BasicTest.class,
        OperatorTest.class
})
public class E2EPrivateOperatorTestSuite {
}
