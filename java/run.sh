#!/usr/bin/env bash

set -e

jar_path=$(ls tstlp-cli/target/*-jar-with-dependencies.jar)

echo $jar_path

java -jar ${jar_path} $@
