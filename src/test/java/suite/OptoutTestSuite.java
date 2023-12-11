package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import suite.optout.BeforeOptoutTest;

@Suite
@SelectClasses(BeforeOptoutTest.class)
public class OptoutTestSuite {
}
