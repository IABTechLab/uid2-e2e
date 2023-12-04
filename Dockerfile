######################
# Build dependencies #
######################
FROM maven:3.9.1-eclipse-temurin-17

WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./src ./src

######################
# Configure env vars #
######################
ENV UID2_E2E_ENV "github-test-pipeline"

ENV UID2_E2E_SITE_ID ""
ENV UID2_E2E_API_KEY ""
ENV UID2_E2E_API_SECRET ""
ENV UID2_E2E_API_KEY_OLD ""
ENV UID2_E2E_API_SECRET_OLD ""

ENV UID2_E2E_IDENTITY_SCOPE ""
ENV UID2_E2E_PHONE_SUPPORT ""

ENV UID2_E2E_PIPELINE_OPERATOR_URL ""
ENV UID2_E2E_PIPELINE_OPERATOR_TYPE ""
ENV UID2_E2E_PIPELINE_OPERATOR_CLOUD_PROVIDER ""

# TODO: UID2-988 - Delete LOCAL_PUBLIC case after optout is integrated into local dev env
CMD \
  if [ "$UID2_E2E_PIPELINE_OPERATOR_TYPE" != "LOCAL_PUBLIC" ] && [ "$UID2_E2E_PIPELINE_OPERATOR_TYPE" != "PUBLIC" ] && [ "$UID2_E2E_PIPELINE_OPERATOR_TYPE" != "PRIVATE" ] ; \
  then \
    echo "ERROR: Incorrect operator type: $UID2_E2E_PIPELINE_OPERATOR_TYPE" ; \
  elif [ "$UID2_E2E_PIPELINE_OPERATOR_TYPE" = "LOCAL_PUBLIC" ] ; \
  then \
    export UID2_E2E_PIPELINE_OPERATOR_TYPE="PUBLIC" ; \
    mvn test -Dtest="E2ELocalPublicOperatorTestSuite" ; \
  elif [ "$UID2_E2E_PIPELINE_OPERATOR_TYPE" = "PUBLIC" ] ; \
  then \
    mvn test -Dtest="E2EPublicOperatorTestSuite" ; \
  else \
    mvn test -Dtest="E2EPrivateOperatorTestSuite" ; \
  fi