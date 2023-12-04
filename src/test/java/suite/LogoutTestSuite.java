package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(suite.logout.BeforeLogoutTest.class)
public class LogoutTestSuite {
}
