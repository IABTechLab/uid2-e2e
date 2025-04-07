#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <test_file_name> <comment>"
    exit 1
fi

TEST_FILE=$1
COMMENT=$2

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource-edited.yml
kubectl delete configmap operator-stress-test

echo "Starting tests"
rm ./k6-test-resource-edited.yml
cp ./k6-test-resource.yml ./k6-test-resource-edited.yml
sed -i -e "s/replaced/$TEST_FILE/g" ./k6-test-resource-edited.yml
sed -i -e "s/replacecomment/$COMMENT/g" ./k6-test-resource-edited.yml

operator_url_value=$OPERATOR_URL
client_key_value=$CLIENT_KEY_VALUE
client_secret_value=$CLIENT_SECRET_VALUE

if [[ -v operator_url_value ]]; then
  if [[ "$OPERATOR_URL" == *"/"* ]]; then
    operator_url_value=$(echo "$OPERATOR_URL" | sed 's/\//\\\//g')
    echo "Escaped OPERATOR_URL: $operator_url_value"
  else
    operator_url_value="$OPERATOR_URL"
    echo "OPERATOR_URL has no slashes: $operator_url_value"
  fi
  sed -i -e "s/operator_url/$operator_url_value/g" ./k6-test-resource-edited.yml
fi

if [[ -v client_key_value ]]; then
  if [[ "$CLIENT_KEY" == *"/"* ]]; then
    client_key_value=$(echo "$CLIENT_KEY" | sed 's/\//\\\//g')
    echo "Escaped CLIENT_KEY: $client_key_value"
  else
    client_key_value="$CLIENT_KEY"
    echo "CLIENT_KEY has no slashes: $client_key_value"
  fi
  sed -i -e "s/client_key/$client_key_value/g" ./k6-test-resource-edited.yml
fi

if [[ -v client_secret_value ]]; then
  if [[ "$CLIENT_SECRET" == *"/"* ]]; then
    client_secret_value=$(echo "$CLIENT_SECRET" | sed 's/\//\\\//g')
    echo "Escaped CLIENT_SECRET: $client_secret_value"
  else
    client_secret_value="$CLIENT_SECRET"
    echo "CLIENT_SECRET has no slashes: $client_secret_value"
  fi
  sed -i -e "s/client_secret/$client_secret_value/g" ./k6-test-resource-edited.yml
fi


kubectl create configmap operator-stress-test --from-file ./$TEST_FILE
kubectl apply -f ./k6-test-resource-edited.yml
