#!/bin/bash

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource-edited-k6-identity-map.yml --ignore-not-found=true
kubectl delete -f ./k6-test-resource-edited-k6-token-generate.yml --ignore-not-found=true
kubectl delete configmap operator-stress-test-k6-identity-map --ignore-not-found=true
kubectl delete configmap operator-stress-test-k6-token-generate --ignore-not-found=true
