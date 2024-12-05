#!/bin/bash

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource-edited.yml
kubectl delete configmap operator-stress-test
