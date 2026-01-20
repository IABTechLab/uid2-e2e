package common;

public final class Const {
    public static final class Config {
        public static final String SUITES = "E2E_SUITES";
        public static final String ARGS_JSON = "E2E_ARGS_JSON";

        public static final String ENV = "E2E_ENV";
        public static final String IDENTITY_SCOPE = "E2E_IDENTITY_SCOPE";
        public static final String PHONE_SUPPORT = "E2E_PHONE_SUPPORT";

        // Local only - Args used for Core E2Es
        public static final class Core {
            public static final String OPERATOR_API_KEY = "UID2_CORE_E2E_OPERATOR_API_KEY";
            public static final String OPTOUT_API_KEY = "UID2_CORE_E2E_OPTOUT_API_KEY";
            public static final String CORE_URL = "UID2_CORE_E2E_CORE_URL";
            public static final String OPTOUT_URL = "UID2_CORE_E2E_OPTOUT_URL";
            public static final String LOCALSTACK_URL = "UID2_CORE_E2E_LOCALSTACK_URL";
        }

        // Args used for Operator E2Es
        public static final class Operator {
            public static final String CLIENT_SITE_ID = "UID2_OPERATOR_E2E_CLIENT_SITE_ID";
            public static final String CLIENT_API_KEY = "UID2_OPERATOR_E2E_CLIENT_API_KEY";
            public static final String CLIENT_API_SECRET = "UID2_OPERATOR_E2E_CLIENT_API_SECRET";

            // Local only - Sharing
            public static final String CLIENT_API_KEY_SHARING_RECIPIENT = "UID2_OPERATOR_E2E_CLIENT_API_KEY_SHARING_RECIPIENT";
            public static final String CLIENT_API_SECRET_SHARING_RECIPIENT = "UID2_OPERATOR_E2E_CLIENT_API_SECRET_SHARING_RECIPIENT";

            public static final String CLIENT_API_KEY_NON_SHARING_RECIPIENT = "UID2_OPERATOR_E2E_CLIENT_API_KEY_NON_SHARING_RECIPIENT";
            public static final String CLIENT_API_SECRET_NON_SHARING_RECIPIENT = "UID2_OPERATOR_E2E_CLIENT_API_SECRET_NON_SHARING_RECIPIENT";

            // Local only - CSTG
            public static final String CSTG_SUBSCRIPTION_ID = "UID2_OPERATOR_E2E_CSTG_SUBSCRIPTION_ID";
            public static final String CSTG_SERVER_PUBLIC_KEY = "UID2_OPERATOR_E2E_CSTG_SERVER_PUBLIC_KEY";
            public static final String CSTG_ORIGIN = "UID2_OPERATOR_E2E_CSTG_ORIGIN";
            public static final String CSTG_INVALID_ORIGIN = "UID2_OPERATOR_E2E_CSTG_INVALID_ORIGIN";
        }

        // Args used for pipeline setup
        public static final class Pipeline {
            public static final String CORE_URL = "UID2_PIPELINE_E2E_CORE_URL";

            public static final String OPERATOR_URL = "UID2_PIPELINE_E2E_OPERATOR_URL";
            public static final String OPERATOR_TYPE = "UID2_PIPELINE_E2E_OPERATOR_TYPE";
            public static final String OPERATOR_CLOUD_PROVIDER = "UID2_PIPELINE_E2E_OPERATOR_CLOUD_PROVIDER";
        }
    }
}
