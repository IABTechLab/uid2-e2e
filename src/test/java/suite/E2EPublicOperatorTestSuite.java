package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;
import suite.operator.*;
import suite.optout.OptOutTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class,
        V0ApiOperatorTest.class,
        V1ApiOperatorTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        OptOutTest.class
})
public class E2EPublicOperatorTestSuite {
}
