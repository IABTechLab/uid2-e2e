# UID2 E2E Test Suites

This repository contains test suites (`src/test/java`) for end-to-end validation of client-facing APIs. The test suites simulate real API requests with mock data and send them to operators via HTTP. This can be done against both mock and real operators.

Any changes to [uid2-operator](https://github.com/IABTechLab/uid2-operator) need to be tested against the Java end-to-end test suites. The same test suites will be executed during all operator release builds.