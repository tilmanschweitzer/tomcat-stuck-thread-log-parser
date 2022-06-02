# report-task-1.md

> Runtime comparison of different ways to count the logs message `notifyStuckThreadDetected` in tomcat logs.

## Environment

    CPU: Intel(R) Core(TM) i9-9880H CPU @ 2.30GHz
    Cores: 8

    Node v18.2.0
    fgrep (BSD grep) 2.5.1-FreeBSD


## Dataset

    ## Full dataset (tomcat-logs/)

    Size: 5.0G
    Lines: 28,953,182

    ## Subset (tomcat-logs/catalina.2022-01)

    Size: 594M
    Lines: 2,924,502


## Use `fgrep`

Baseline with `fgrep`.

### Results for full dataset

Command: `fgrep -c notifyStuckThreadDetected tomcat-logs/*` (repeat 3 times)

    real 1m23.124s
    user 1m20.405s
    sys 0m1.003s

    real 1m16.965s
    user 1m15.619s
    sys 0m0.976s

    real 1m17.367s
    user 1m16.021s
    sys 0m0.977s


### Results for subset

Command: `fgrep -c notifyStuckThreadDetected tomcat-logs/catalina.2022-01*` (repeat 3 times)

    real 0m8.834s
    user 0m8.723s
    sys 0m0.101s

    real 0m8.823s
    user 0m8.710s
    sys 0m0.101s

    real 0m8.912s
    user 0m8.795s
    sys 0m0.102s


## Use `rg` (ripgrep)

Benchmark with `rg --sort-files`.

### Results for full dataset

Command: `rg -c --sort-files notifyStuckThreadDetected tomcat-logs/*` (repeat 3 times)

    real 0m1.018s
    user 0m0.351s
    sys 0m0.663s

    real 0m1.030s
    user 0m0.359s
    sys 0m0.668s

    real 0m1.029s
    user 0m0.356s
    sys 0m0.670s


### Results for subset

Command: `rg -c --sort-files notifyStuckThreadDetected tomcat-logs/catalina.2022-01*` (repeat 3 times)

    real 0m0.132s
    user 0m0.047s
    sys 0m0.083s

    real 0m0.130s
    user 0m0.047s
    sys 0m0.082s

    real 0m0.130s
    user 0m0.046s
    sys 0m0.081s


## Node.js `fs.readFileSync` (sync)

Run sequentially using `fs.readFileSync`.

### Results for full dataset

Command: `node node/tstlp-sync.mjs tomcat-logs/` (repeat 3 times)

    real 0m14.624s
    user 0m12.324s
    sys 0m3.446s

    real 0m14.587s
    user 0m12.269s
    sys 0m3.452s

    real 0m14.885s
    user 0m12.544s
    sys 0m3.500s


### Results for subset

Command: `node node/tstlp-sync.mjs tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m1.760s
    user 0m1.416s
    sys 0m0.442s

    real 0m1.756s
    user 0m1.412s
    sys 0m0.442s

    real 0m1.759s
    user 0m1.410s
    sys 0m0.449s


## Node.js `fsPromises.readFile` (parallel with `Promise.all`)

Run parallel, but wait for all results with `Promise.all`.

### Results for full dataset

Command: `node node/tstlp-promise-all.mjs tomcat-logs/` (repeat 3 times)

    real 0m14.367s
    user 0m13.006s
    sys 0m6.380s

    real 0m14.150s
    user 0m12.851s
    sys 0m6.166s

    real 0m14.383s
    user 0m13.013s
    sys 0m6.149s


### Results for subset

Command: `node node/tstlp-promise-all.mjs tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m1.652s
    user 0m1.426s
    sys 0m0.704s

    real 0m1.625s
    user 0m1.455s
    sys 0m0.714s

    real 0m1.646s
    user 0m1.479s
    sys 0m0.712s


## Node.js `fsPromises.readFile` (async/await)

Run in sequential order with async/await.

### Results for full dataset

Command: `node node/tstlp-async-await.mjs tomcat-logs/` (repeat 3 times)

    real 0m15.441s
    user 0m13.251s
    sys 0m3.626s

    real 0m15.320s
    user 0m13.184s
    sys 0m3.640s

    real 0m15.519s
    user 0m13.362s
    sys 0m3.614s


### Results for subset

Command: `node node/tstlp-async-await.mjs tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m1.831s
    user 0m1.543s
    sys 0m0.459s

    real 0m1.828s
    user 0m1.538s
    sys 0m0.458s

    real 0m1.858s
    user 0m1.558s
    sys 0m0.473s


## Node.js `fsPromises.readFile` (parallel)

Run parallel but ensure sequential output.

### Results for full dataset

Command: `node node/tstlp-parallel.mjs tomcat-logs/` (repeat 3 times)

    real 0m14.297s
    user 0m12.856s
    sys 0m6.036s

    real 0m14.286s
    user 0m13.028s
    sys 0m6.163s

    real 0m14.061s
    user 0m12.760s
    sys 0m6.006s


### Results for subset

Command: `node node/tstlp-parallel.mjs tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m1.641s
    user 0m1.464s
    sys 0m0.716s

    real 0m1.716s
    user 0m1.482s
    sys 0m0.707s

    real 0m1.636s
    user 0m1.420s
    sys 0m0.672s


## Node.js with worker threads

Run parallel and split up workload to worker threads

### Results for full dataset

Command: `node node/tstlp-worker-threads.mjs tomcat-logs/` (repeat 3 times)

    real 0m4.517s
    user 0m20.700s
    sys 0m11.067s

    real 0m4.285s
    user 0m20.829s
    sys 0m11.131s

    real 0m4.174s
    user 0m20.766s
    sys 0m10.937s


### Results for subset

Command: `node node/tstlp-worker-threads.mjs tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m0.656s
    user 0m3.687s
    sys 0m1.870s

    real 0m0.671s
    user 0m3.685s
    sys 0m1.905s

    real 0m0.644s
    user 0m3.666s
    sys 0m1.889s


## Go (sync)

Run sequentially with a very basic go program

### Results for full dataset

Command: `go run go/tstlp-sync.go tomcat-logs/` (repeat 3 times)

    real 0m6.646s
    user 0m5.624s
    sys 0m2.028s

    real 0m6.538s
    user 0m5.453s
    sys 0m1.944s

    real 0m6.468s
    user 0m5.315s
    sys 0m1.943s


### Results for subset

Command: `go run go/tstlp-sync.go tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m1.074s
    user 0m0.819s
    sys 0m0.494s

    real 0m1.076s
    user 0m0.790s
    sys 0m0.481s

    real 0m1.065s
    user 0m0.810s
    sys 0m0.509s


## Go (parallel)

Run parallel with very basic go routines

### Results for full dataset

Command: `go run go/tstlp-parallel.go tomcat-logs/` (repeat 3 times)

    real 0m2.346s
    user 0m11.801s
    sys 0m5.656s

    real 0m2.487s
    user 0m13.354s
    sys 0m6.936s

    real 0m2.480s
    user 0m12.964s
    sys 0m6.802s


### Results for subset

Command: `go run go/tstlp-parallel.go tomcat-logs/ catalina.2022-01` (repeat 3 times)

    real 0m0.577s
    user 0m1.754s
    sys 0m1.206s

    real 0m0.569s
    user 0m1.739s
    sys 0m1.194s

    real 0m0.574s
    user 0m1.753s
    sys 0m1.209s


