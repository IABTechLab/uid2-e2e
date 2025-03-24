# UID2 E2E Test Suites

This repository contains test suites (`src/test/java`) for end-to-end validation of client-facing APIs. The test suites simulate real API requests with mock data and send them to operators via HTTP. This can be done against both mock and real operators.

Any changes to [uid2-operator](https://github.com/IABTechLab/uid2-operator) need to be tested against the Java end-to-end test suites. The same test suites will be executed during all operator release builds.

## Test Suites

There are different test suites that can be run depending on the environment and operator type:

| Test Suite                    | Description                         |
|-------------------------------|-------------------------------------|
| `E2ELocalFullTestSuite`       | Used to test all apps locally       |
| `E2ECoreTestSuite`            | Used when testing Core              |
| `E2EPublicOperatorTestSuite`  | Used when testing public Operators  |
| `E2EPrivateOperatorTestSuite` | Used when testing private Operators |

## Environment Variables

* `E2E_SUITES` - **Docker image only** - The test suites to run, comma separated
  *  e.g. `E2EPrivateOperatorTestSuite,E2ECoreTestSuite`
* `E2E_ARGS_JSON` - The below environment variables can be put into this environment variable as a JSON
    * Any environment variables declared explicitly will override args in `E2E_ARGS_JSON`

### General

| Name                 | Value                                                                                                                                                 |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `E2E_ENV`            | The E2E environment - this determines which apps get instantiated<br/>Certain tests run for `local` environments only<br/>Check `AppsMap` for details |
| `E2E_IDENTITY_SCOPE` | The identity scope - one of [UID2, EUID]                                                                                                              |
| `E2E_PHONE_SUPPORT`  | True if APIs support phone numbers, false otherwise                                                                                                   |

### Core

| Name                             | Value                                                |
|----------------------------------|------------------------------------------------------|
| `UID2_CORE_E2E_OPERATOR_API_KEY` | The API key for an Operator to communicate with Core |
| `UID2_CORE_E2E_OPTOUT_API_KEY`   | The API key for Optout to communicate with Core      |
| `UID2_CORE_E2E_CORE_URL`         | The Core URL to include in attestation requests      |
| `UID2_CORE_E2E_OPTOUT_URL`       | The Optout URL to include in attestation requests    |

### Operator

| Name                                                        | Value                                                                                                              |
|-------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `UID2_OPERATOR_E2E_CLIENT_SITE_ID`                          | The site ID of the client communicating with Operator                                                              |
| `UID2_OPERATOR_E2E_CLIENT_API_KEY`                          | The API key for a client to communicate with Operator                                                              |
| `UID2_OPERATOR_E2E_CLIENT_API_SECRET`                       | The API secret for a client to communicate with Operator                                                           |
| `UID2_OPERATOR_E2E_CLIENT_API_KEY_BEFORE_OPTOUT_CUTOFF`     | **Optout cutoff tests** - The API key before the optout policy cutoff for a client to communicate with Operator    |
| `UID2_OPERATOR_E2E_CLIENT_API_SECRET_BEFORE_OPTOUT_CUTOFF`  | **Optout cutoff tests** - The API secret before the optout policy cutoff for a client to communicate with Operator |
| `UID2_OPERATOR_E2E_CLIENT_API_KEY_SHARING_RECIPIENT`        | **Sharing tests** - The API key with SHARER role for a client to communicate with Operator                         |
| `UID2_OPERATOR_E2E_CLIENT_API_SECRET_SHARING_RECIPIENT`     | **Sharing tests** - The API with SHARER role secret for a client to communicate with Operator                      |
| `UID2_OPERATOR_E2E_CLIENT_API_KEY_NON_SHARING_RECIPIENT`    | **Sharing tests** - The API key without SHARER role for a client to communicate with Operator                      |
| `UID2_OPERATOR_E2E_CLIENT_API_SECRET_NON_SHARING_RECIPIENT` | **Sharing tests** - The API without SHARER role secret for a client to communicate with Operator                   |
| `UID2_OPERATOR_E2E_CSTG_SUBSCRIPTION_ID`                    | **CSTG tests** - The subscription ID                                                                               |
| `UID2_OPERATOR_E2E_CSTG_SERVER_PUBLIC_KEY`                  | **CSTG tests** - The server public key                                                                             |
| `UID2_OPERATOR_E2E_CSTG_ORIGIN`                             | **CSTG tests** - A valid origin                                                                                    |
| `UID2_OPERATOR_E2E_CSTG_INVALID_ORIGIN`                     | **CSTG tests** - An invalid origin                                                                                 |

### Pipeline

| Name                                        | Value                                                                                                         |
|---------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| `UID2_PIPELINE_E2E_CORE_URL`                | The Core URL                                                                                                  |
| `UID2_PIPELINE_E2E_OPERATOR_URL`            | The Operator URL                                                                                              |
| `UID2_PIPELINE_E2E_OPERATOR_TYPE`           | The type of Operator - one of [PUBLIC, PRIVATE]                                                               |
| `UID2_PIPELINE_E2E_OPERATOR_CLOUD_PROVIDER` | Empty for public Operators, the cloud provider for private Operators - one of [aws-nitro, gcp-oidc, azure-cc] |

## Running the Dockerfile

```shell
docker build -f Dockerfile -t uid2-e2e .
docker run --env <ENV>=<VAR> ... uid2-e2e
 ```
* If running the E2E tests against localhost, include the option `--network=host`
