package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;
import suite.operator.*;
import suite.optout.OptoutTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class,
        OperatorTest.class,
        OperatorPublicOnlyTest.class,
        OperatorLocalOnlyTest.class,
        OptoutTest.class
})
public class E2EPublicOperatorTestSuite {
}
