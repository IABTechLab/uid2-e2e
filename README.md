# UID2 E2E Test Suites

This repository contains test suites (`src/test/java`) for end-to-end validation of client-facing APIs. The test suites simulate real API requests with mock data and send them to operators via HTTP. This can be done against both mock and real operators.

Any changes to [uid2-operator](https://github.com/IABTechLab/uid2-operator) need to be tested against the Java end-to-end test suites. The same test suites will be executed during all operator release builds.

## Test Suites

There are different test suites that can be run depending on the environment and operator type:

| Test Suite                            | Description                                                  |
|---------------------------------------|--------------------------------------------------------------|
| `E2ELocalFullTestSuite`               | Used when running both public and private operators locally. |
| `E2EPipelinePrivateOperatorTestSuite` | Used when testing private operators in a pipeline.           |
| `E2EPipelinePublicOperatorTestSuite`  | Used when testing public operators in a pipeline.            |
| `E2EPrivateOperatorTestSuite`         | Used when testing real private operators.                    |
| `E2EPublicOperatorTestSuite`          | Used when testing real public operators.                     |

## Running the Dockerfile

`docker build -f Dockerfile -t uid2-e2e . && docker run --env <ENV>=<VAR> ... uid2-e2e`
* Set each environment variable specified in the Dockerfile as `--env <ENV>=<VAR>`
* If running the E2E tests against localhost, include the option `--network=host`
