#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <test_file_name> <comment>"
    exit 1
fi

TEST_FILE=$1
COMMENT=$2

echo "Delete existing tests"
echo kubectl delete -f ./k6-test-resource-edited.yml
echo kubectl delete configmap operator-stress-test

echo "Starting tests"
rm ./k6-test-resource-edited.yml
cp ./k6-test-resource.yml ./k6-test-resource-edited.yml
sed -i -e "s/replaced/$TEST_FILE/g" ./k6-test-resource-edited.yml
sed -i -e "s/replacecomment/$COMMENT/g" ./k6-test-resource-edited.yml

echo kubectl create configmap operator-stress-test --from-file ./$TEST_FILE
echo kubectl apply -f ./k6-test-resource-edited.yml