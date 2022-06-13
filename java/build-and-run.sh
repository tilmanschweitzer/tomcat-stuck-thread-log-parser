#!/usr/bin/env bash

set -e

cd tstlp-core && mvn clean install
cd ..
mvn clean compile assembly:single

./run.sh $@
