# Tomcat Stuck Thread Log Parser

> Compare different implementations to count stuck threads in multiple log files.

After an initial comparison, further features will be implemented in Java.

## Test all implementations (with bats)

First, install [bats](https://bats-core.readthedocs.io/en/stable/installation.html).

Run tests

    bats tests.bats

## Generate custom performance comparison report

    # Script with folder and prefix for a custom subset
    ./generate-report.sh ./tomcat-log-examples example-2022-06-01 

The example reports in [count-stuck-threads.md](count-stuck-threads.md) was generated with a private 5 GB dataset which is not attached to the repository for several reasons.

