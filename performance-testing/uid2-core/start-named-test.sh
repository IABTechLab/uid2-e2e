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

core_url_value=$CORE_URL
operator_key_value=$OPERATOR_KEY

if [[ -v core_url_value ]]; then
  if [[ "$CORE_URL" == *"/"* ]]; then
    core_url_value=$(echo "$CORE_URL" | sed 's/\//\\\//g')
    echo "Escaped CORE_URL: $core_url_value"
  else
    core_url_value="$CORE_URL"
    echo "CORE_URL has no slashes: $core_url_value"
  fi
  sed -i -e "s/core_url/$core_url_value/g" ./k6-test-resource-edited.yml
fi

if [[ -v operator_key_value ]]; then
  if [[ "$OPERATOR_KEY" == *"/"* ]]; then
    operator_key_value=$(echo "$OPERATOR_KEY" | sed 's/\//\\\//g')
    echo "Escaped OPERATOR_KEY: $operator_key_value"
  else
    operator_key_value="$OPERATOR_KEY"
    echo "OPERATOR_KEY has no slashes: $operator_key_value"
  fi
  sed -i -e "s/operator_key/$operator_key_value/g" ./k6-test-resource-edited.yml
fi


kubectl create configmap operator-stress-test --from-file ./$TEST_FILE
kubectl apply -f ./k6-test-resource-edited.yml
