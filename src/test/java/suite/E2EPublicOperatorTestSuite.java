package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;import suite.operator.V0ApiOperatorTest;
import suite.operator.V1ApiOperatorTest;
import suite.operator.V2ApiOperatorPublicOnlyTest;
import suite.operator.V2ApiOperatorTest;
import suite.optout.BeforeOptoutTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class,
        V0ApiOperatorTest.class,
        V1ApiOperatorTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        BeforeOptoutTest.class
})
public class E2EPublicOperatorTestSuite {
}
