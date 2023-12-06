package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BasicTestSuite.class,
        OperatorTestSuite.class
})
public class E2ELocalPublicOperatorTestSuite {
    // TODO: UID2-988 - Delete this class after optout is integrated into local dev env
}
