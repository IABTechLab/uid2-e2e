package common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class EnvUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvUtil.class);
    private static final Map<String, String> ARGS = new HashMap<>();

    static {
        try {
            String args = getEnv(Const.Config.ARGS_JSON, false);

            if (StringUtils.isNotBlank(args)) {
                TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
                };
                ARGS.putAll(Mapper.OBJECT_MAPPER.readValue(args, typeRef));
            }
        } catch (JsonProcessingException e) {
            LOGGER.error(e::getMessage);
            System.exit(1);
        }
    }

    private EnvUtil() {
    }

    public static String getEnv(String env, boolean required) {
        String value = System.getenv(env);
        if (StringUtils.isBlank(value)) {
            value = ARGS.get(env);
        }

        if (StringUtils.isBlank(value) && required) {
            LOGGER.error(() -> "Missing environment variable: " + env);
            System.exit(1);
        }

        return value;
    }

    public static String getEnv(String env) {
        return getEnv(env, true);
    }
}
