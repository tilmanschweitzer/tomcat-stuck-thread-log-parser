#!/usr/bin/env bash

set -e

atlas-mvn clean compile assembly:single

./run.sh $@
