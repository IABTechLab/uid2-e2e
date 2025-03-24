package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreRefreshTest;
import suite.core.CoreTest;
import suite.operator.*;
import suite.optout.OptoutTest;
import suite.validator.V0ApiValidatorTest;
import suite.validator.V1ApiValidatorTest;
import suite.validator.V2ApiValidatorTest;

@Suite
@SelectClasses({
        BasicTest.class,
        CoreTest.class,
        CoreRefreshTest.class,
        V0ApiOperatorTest.class,
        V1ApiOperatorTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        V2ApiOperatorLocalOnlyTest.class,
        OptoutTest.class,
        V0ApiValidatorTest.class,
        V1ApiValidatorTest.class,
        V2ApiValidatorTest.class
})
public class E2ELocalFullTestSuite {
}
