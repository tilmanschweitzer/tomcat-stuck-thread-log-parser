#!/usr/bin/env bash

set -e

jar_path=$(ls target/*.jar)

java -jar ${jar_path} $@
