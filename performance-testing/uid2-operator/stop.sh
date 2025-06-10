#!/bin/bash

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource-edited-k6-identity-map.yml
kubectl delete -f ./k6-test-resource-edited-k6-token-generate.yml
kubectl delete configmap operator-stress-test-k6-identity-map
kubectl delete configmap operator-stress-test-k6-token-generate
