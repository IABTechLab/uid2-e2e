# UID2 Operator Performance Testing Tool

## Steps

### Step 1 - Deploy Operator Instance
In order to reduce overhead on client side, this tool generates an encrypted request first, then uses it for all load testing requests. Therefore we need to disable the timestamp check of operator, use https://github.com/IABTechLab/uid2-operator/commit/979d43076ce94b80622f9c7d9eed87d97fc71a7b as an example.

You can deploy an operator instance as a Docker container by providing required environment variables.

Save and modify following environment variables as `env.txt` file. 
```
optout_api_token=<TOKEN>
core_api_token=<TOKEN>
service_verbose=true
service_instances=16
storage_mock=false
optout_s3_path_compat=false
optout_data_dir=/opt/uid2/operator-optout/
optout_bloom_filter_size=8192
optout_delta_rotate_interval=300
optout_delta_backtrack_in_days=1
optout_partition_interval=86400
optout_max_partitions=30
optout_heap_default_capacity=8192
cloud_download_threads=8
cloud_upload_threads=2
cloud_refresh_interval=60
optout_inmem_cache=true
identity_token_expires_after_seconds=86400
refresh_token_expires_after_seconds=2592000
refresh_identity_token_after_seconds=3600
enforce_https=true
allow_legacy_api=false
clients_metadata_path=https://core-integ.uidapi.com/clients/refresh
keys_metadata_path=https://core-integ.uidapi.com/key/refresh
keys_acl_metadata_path=https://core-integ.uidapi.com/key/acl/refresh
salts_metadata_path=https://core-integ.uidapi.com/salt/refresh
optout_metadata_path=https://optout-integ.uidapi.com/optout/refresh
core_attest_url=https://core-integ.uidapi.com/attest
optout_api_uri=https://optout-integ.uidapi.com/optout/replicate
optout_s3_folder=uid-optout-integ/
KUBERNETES_SERVICE_HOST=mock
```

Run following command to start an operator container, remember to change image tag accordingly.
```shell
docker run -d --env-file env.txt -p 8080:8080 ghcr.io/iabtechlab/uid2-operator:4.3.6-SNAPSHOT-default
```

### Step 2 - Execute K6 Script
You can manually modify the scenarios in the script to test on a single endpoint with different settings.

For the following options, you can get a valid `REFRESH_TOKEN` from a recent successful `/token/generate` response.
In the future, we should upgrade the k6 script to get refresh token automatically.

#### Option 2a - Execute K6 Script Locally (uid2-dev-workspace)
If you would like to test locally, follow these steps:
1. Pull the K6 Docker image: `docker pull grafana/k6`
2. Execute the K6 script
    * PowerShell:
        ```
        cat k6-uid2-operator.js `
        | docker run --network="host" --rm -i `
          -e CLIENT_KEY="UID2-C-L-999-fCXrMM.fsR3mDqAXELtWWMS+xG1s7RdgRTMqdOH2qaAo=" `
          -e CLIENT_SECRET="DzBzbjTJcYL0swDtFs2krRNu+g1Eokm2tBU4dEuD0Wk=" `
          -e BASE_URL="http://localhost:8080" `
          -e REFRESH_TOKEN="<REFRESH_TOKEN>" `
          grafana/k6 run -
        ```

#### Option 2b - Execute K6 Script in K8s
In order to reduce network latency, we should deploy k6 and its script with the same zone of UID2 operator.

Set environment variables `CLIENT_KEY`, `CLIENT_SECRET`, `BASE_URL` and `REFRESH_TOKEN`, then use k6 to execute the testing by following command.
```
k6 run k6-uid2-operator.js -e CLIENT_KEY=$CLIENT_KEY -e CLIENT_SECRET=$CLIENT_SECRET -e BASE_URL=$BASE_URL -e REFRESH_TOKEN=$REFRESH_TOKEN
```

