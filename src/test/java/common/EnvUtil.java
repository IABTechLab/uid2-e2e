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
    private static final Map<String, String> ARGS;

    static {
        try {
            String args = getEnv(Args.ARGS_JSON);
            TypeReference<HashMap<String,String>> typeRef = new TypeReference<>() {};
            ARGS = Mapper.OBJECT_MAPPER.readValue(args, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private EnvUtil() {
    }

    public static String getEnv(String env) {
        String value = System.getenv(env);
        if (StringUtils.isBlank(value)) {
            value = ARGS.get(env);
        }

        if (StringUtils.isBlank(value)) {
            LOGGER.error(() -> "Missing environment variable: " + env);
            System.exit(1);
        }

        return value;
    }
}
