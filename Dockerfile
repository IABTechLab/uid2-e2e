######################
# Build dependencies #
######################
FROM maven:3.9.8-eclipse-temurin-21

WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./src ./src

######################
# Configure env vars #
######################
ENV E2E_SUITES ""
ENV E2E_ARGS_JSON ""

ENV E2E_ENV ""
ENV E2E_IDENTITY_SCOPE ""
ENV E2E_PHONE_SUPPORT ""

ENV UID2_CORE_E2E_OPERATOR_API_KEY ""
ENV UID2_CORE_E2E_OPTOUT_API_KEY ""
ENV UID2_CORE_E2E_CORE_URL ""
ENV UID2_CORE_E2E_OPTOUT_URL ""

ENV UID2_OPERATOR_E2E_CLIENT_SITE_ID ""
ENV UID2_OPERATOR_E2E_CLIENT_API_KEY ""
ENV UID2_OPERATOR_E2E_CLIENT_API_SECRET ""
ENV UID2_OPERATOR_E2E_CLIENT_API_KEY_SHARING_RECIPIENT ""
ENV UID2_OPERATOR_E2E_CLIENT_API_SECRET_SHARING_RECIPIENT ""
ENV UID2_OPERATOR_E2E_CLIENT_API_KEY_NON_SHARING_RECIPIENT ""
ENV UID2_OPERATOR_E2E_CLIENT_API_SECRET_NON_SHARING_RECIPIENT ""
ENV UID2_OPERATOR_E2E_CSTG_SUBSCRIPTION_ID ""
ENV UID2_OPERATOR_E2E_CSTG_SERVER_PUBLIC_KEY ""
ENV UID2_OPERATOR_E2E_CSTG_ORIGIN ""
ENV UID2_OPERATOR_E2E_CSTG_INVALID_ORIGIN ""

ENV UID2_PIPELINE_E2E_CORE_URL ""
ENV UID2_PIPELINE_E2E_OPERATOR_URL ""
ENV UID2_PIPELINE_E2E_OPERATOR_TYPE ""
ENV UID2_PIPELINE_E2E_OPERATOR_CLOUD_PROVIDER ""

CMD mvn test -Dtest="${E2E_SUITES}"
