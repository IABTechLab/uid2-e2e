#!/bin/bash

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource.yml
kubectl delete configmap operator-stress-test

echo "Starting tests"
kubectl create configmap operator-stress-test --from-file ./k6-uid2-operator-encrypt-inline.js
kubectl apply -f ./k6-test-resource.yml