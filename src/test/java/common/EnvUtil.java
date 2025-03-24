package common;

import org.apache.commons.lang3.StringUtils;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

public final class EnvUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvUtil.class);

    private EnvUtil() {
    }

    public static String getEnv(String env) {
        String value = System.getenv(env);
        if (StringUtils.isBlank(value)) {
            LOGGER.error(() -> "Missing environment variable: " + env);
            System.exit(1);
        }
        return value;
    }
}
