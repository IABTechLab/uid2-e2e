# UID2 Operator Performance Testing Tool
The following instructions will work for any operator.

## Steps
### Step 1 - Configure the K6 script
The script as checked in has a basic config that will run a check against all endpoints.


All the manual config can be changed at the top of the script. 

To change/remove scenarios modify the options.scenarios. For information on configuring k6 scenarios see https://k6.io/docs/using-k6/scenarios/.

The variable `testDurationInSeconds` must be greater than the total duration of the test you are running.
Any of the `*Tests` booleans can be set false if you are not running tests for that endpoint, this will save memory and start up time.
### Step 2 - Execute K6 Script
#### Option 2a - Execute K6 Script Locally (uid2-dev-workspace)
If you would like to test locally, follow these steps:
1. Pull the K6 Docker image: `docker pull grafana/k6`
2. Execute the K6 script
    * PowerShell/Bash:
        ```
        cat k6-uid2-operator.js `
        | docker run --network="host" --rm -i `
          -e CLIENT_KEY="<client_key>" `
          -e CLIENT_SECRET="<client_secret>" `
          -e BASE_URL="<operator_endpoint>" `
          grafana/k6 run -
        ```

#### Option 2b - Execute K6 Script in K8s
In order to reduce network latency, we should deploy k6 and its script with the same zone of UID2 operator.

Follow the installation instructions here: https://grafana.com/blog/2022/06/23/running-distributed-load-tests-on-kubernetes


Set environment variables `CLIENT_KEY`, `CLIENT_SECRET`, `BASE_URL` and then use k6 to execute the testing by following command.
```
k6 run k6-uid2-operator.js -e CLIENT_KEY=$CLIENT_KEY -e CLIENT_SECRET=$CLIENT_SECRET -e BASE_URL=$BASE_URL -e REFRESH_TOKEN=$REFRESH_TOKEN
```

