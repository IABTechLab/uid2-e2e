package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.basic.BasicTest;
import suite.operator.*;
import suite.optout.BeforeOptoutTest;

@Suite
@SelectClasses({
        BasicTest.class,
        V0ApiOperatorTest.class,
        V1ApiOperatorTest.class,
        V2ApiOperatorTest.class,
        V2ApiOperatorLocalOnlyTest.class,
        V2ApiOperatorPublicOnlyTest.class,
        BeforeOptoutTest.class
})
public class E2EPipelinePublicOperatorTestSuite {
}
