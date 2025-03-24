package common;

public final class Args {
    public static final String ENV = "E2E_ENV";
    public static final String IDENTITY_SCOPE = "E2E_IDENTITY_SCOPE";
    public static final String PHONE_SUPPORT = "E2E_PHONE_SUPPORT";

    // Args used for Operator E2Es
    public static final class Operator {
        public static final String CLIENT_SITE = "UID2_OPERATOR_E2E_CLIENT_SITE";
        public static final String CLIENT_API_KEY = "UID2_OPERATOR_E2E_CLIENT_API_KEY";
        public static final String CLIENT_API_SECRET = "UID2_OPERATOR_E2E_CLIENT_API_SECRET";

        // Optout policy check
        public static final String CLIENT_API_KEY_PRE_OPTOUT_POLICY = "UID2_OPERATOR_E2E_CLIENT_API_KEY_PRE_OPTOUT_POLICY";
        public static final String CLIENT_API_SECRET_PRE_OPTOUT_POLICY = "UID2_OPERATOR_E2E_CLIENT_API_SECRET_PRE_OPTOUT_POLICY";

        // Sharing
        public static final String CLIENT_API_KEY_SHARING_PARTICIPANT = "UID2_OPERATOR_E2E_CLIENT_API_KEY_SHARING_PARTICIPANT";
        public static final String CLIENT_API_KEY_SHARING_NON_PARTICIPANT = "UID2_OPERATOR_E2E_CLIENT_API_KEY_SHARING_NON_PARTICIPANT";
        public static final String CLIENT_API_SECRET_SHARING_PARTICIPANT = "UID2_OPERATOR_E2E_CLIENT_API_SECRET_SHARING_PARTICIPANT";
        public static final String CLIENT_API_SECRET_NON_SHARING_PARTICIPANT = "UID2_OPERATOR_E2E_CLIENT_API_SECRET_NON_SHARING_PARTICIPANT";

        // CSTG
        public static final String CSTG_SUBSCRIPTION_ID = "UID2_OPERATOR_E2E_CSTG_SUBSCRIPTION_ID";
        public static final String CSTG_PUBLIC_KEY = "UID2_OPERATOR_E2E_CSTG_PUBLIC_KEY";
        public static final String CSTG_ORIGIN = "UID2_OPERATOR_E2E_CSTG_ORIGIN";
        public static final String CSTG_INVALID_ORIGIN = "UID2_OPERATOR_E2E_CSTG_INVALID_ORIGIN";
    }

    // Args used for Core E2Es
    public static final class Core {
        public static final String OPERATOR_API_KEY = "UID2_CORE_E2E_OPERATOR_API_KEY";
        public static final String OPTOUT_API_KEY = "UID2_CORE_E2E_OPTOUT_API_KEY";
        public static final String CORE_URL = "UID2_CORE_E2E_CORE_URL";
        public static final String OPTOUT_URL = "UID2_CORE_E2E_OPTOUT_URL";
    }
}
