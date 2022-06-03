#!/usr/bin/env bash

set -e

mvn clean compile assembly:single

./run.sh $@
