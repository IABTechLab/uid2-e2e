package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;
import suite.operator.V2ApiOperatorPublicOnlyTest;
import suite.operator.V2ApiOperatorTest;
import suite.optout.OptoutTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        OptoutTest.class
})
public class E2EPublicOperatorTestSuite {
}
