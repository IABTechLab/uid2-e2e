#!/bin/bash

COMMENT=$1

if [ "$#" -ne 1 ]; then
    COMMENT=$( date '+%F_%H:%M:%S' )
fi

./start-named-test.sh k6-token-generate.js $COMMENT