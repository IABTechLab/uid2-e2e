package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.core.CoreTest;
import suite.operator.*;
import suite.optout.BeforeOptoutTest;
import suite.validator.V0ApiValidatorTest;
import suite.validator.V2ApiValidatorTest;

@Suite
@SelectClasses({
        BasicTest.class,
        V0ApiOperatorTest.class,
        V1ApiOperatorTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        BeforeOptoutTest.class,
        CoreTest.class
})
public class E2EPublicOperatorTestSuite {
}
