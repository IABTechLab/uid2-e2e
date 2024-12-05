#!/bin/bash

COMMENT=$1

if [ "$S#" -ne 1 ]; then
    COMMENT=$( date '+%F_%H:%M:%S' )
fi

./start.sh ./k6-token-generate.js $COMMENT