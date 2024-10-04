#!/bin/bash

echo "Delete existing tests"
kubectl delete -f ./k6-test-resource.yml
kubectl delete configmap operator-stress-test