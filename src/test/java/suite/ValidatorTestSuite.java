package suite;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("suite.validator")
public class ValidatorTestSuite {
}
