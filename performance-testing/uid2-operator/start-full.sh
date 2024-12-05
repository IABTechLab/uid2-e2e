#!/bin/bash

COMMENT=$1

if [ "$S#" -ne 1 ]; then
    COMMENT=$( date '+%F_%H:%M:%S' )
fi

./start-named-test.sh ./k6-token-generate-refresh-identitymap.js $COMMENT