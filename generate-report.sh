#!/usr/bin/env bash

filename="count-stuck-threads.md"
folder="tomcat-logs"
subset_prefix="catalina.2022-01"
subset_patterns="$folder/$subset_prefix*"

echo "# $filename

> Runtime comparison of different ways to count the logs message \`notifyStuckThreadDetected\` in tomcat logs.
" > $filename


## Add machine information (currently depending on MacOS)
echo "## Environment

    CPU:$(sysctl -a | fgrep cpu.brand_string | cut -d ":" -f 2)
    Cores:$(sysctl -a | fgrep machdep.cpu.core_count | cut -d ":" -f 2)

    Node $(node --version)
    $(fgrep --version)

" >> $filename

## Add dataset information
export LC_NUMERIC="en_US.UTF-8"
echo "## Dataset

    ## Full dataset ($folder/)

    Size: $(du -hsc $folder | fgrep total | cut -f 1)
    Lines: $(cat $folder/* | wc -l | xargs printf "%'.3d\n")

    ## Subset ($folder/$subset_prefix)

    Size: $(du -hsc $subset_patterns | fgrep total | cut -f 1)
    Lines: $(cat $subset_patterns  | wc -l | xargs printf "%'.3d\n")

" >> $filename


function reportForCommand() {
    title=$1
    description=$2
    full_dataset_cmd=$3
    full_dataset_repeat=$4

    subset_cmd=$5
    subset_repeat=$6

    printf "## $title\n\n$description\n\n" >> $filename

    if [[ -n "$full_dataset_cmd" ]]; then
        printf "### Results for full dataset\n\n" >> $filename
         printf "Command: \`$full_dataset_cmd\` (repeat $full_dataset_repeat times)\n\n" >> $filename
         i=0
         while [ $i -lt $full_dataset_repeat ]
         do
             { time $full_dataset_cmd > /dev/null; } 2>&1 | xargs printf "    %s %s\n" >> $filename
             echo "" >> $filename
             let "i+=1"
         done
          echo "" >> $filename
    fi

    if [[ -n "$subset_cmd" ]]; then
        printf "### Results for subset\n\n" >> $filename
         printf "Command: \`$subset_cmd\` (repeat $subset_repeat times)\n\n" >> $filename
         i=0
         while [ $i -lt $subset_repeat ]
         do
             { time $subset_cmd > /dev/null; } 2>&1 | xargs printf "    %s %s\n" >> $filename
             echo "" >> $filename
             let "i+=1"
         done
          echo "" >> $filename
    fi
}

full_dataset_repeat=3
subset_repeat=3

# fgrep as baseline
fgrep_title="Use \`fgrep\`"
fgrep_desc="Baseline with \`fgrep\`."
reportForCommand "$fgrep_title" "$fgrep_desc" "fgrep -c notifyStuckThreadDetected $folder/*" $full_dataset_repeat "fgrep -c notifyStuckThreadDetected $folder/$subset_prefix*" $subset_repeat

# ripgrep
ripgrep_title="Use \`rg\` (ripgrep)"
ripgrep_desc="Benchmark with \`rg --sort-files\`."
reportForCommand "$ripgrep_title" "$ripgrep_desc" "rg -c --sort-files notifyStuckThreadDetected $folder/*" $full_dataset_repeat "rg -c --sort-files notifyStuckThreadDetected $folder/$subset_prefix*" $subset_repeat

# Node.js (sync)
node_sync_title="Node.js \`fs.readFileSync\` (sync)"
node_sync_desc="Run sequentially using \`fs.readFileSync\`."
node_sync_script="node/tstlp-sync.mjs"
reportForCommand "$node_sync_title" "$node_sync_desc" "node $node_sync_script $folder/" $full_dataset_repeat "node $node_sync_script $folder/ $subset_prefix" $subset_repeat

# Node.js (Promise.all)
node_p_all_title="Node.js \`fsPromises.readFile\` (parallel with \`Promise.all\`)"
node_p_all_desc="Run parallel, but wait for all results with \`Promise.all\`."
node_p_all_script="node/tstlp-promise-all.mjs"
reportForCommand "$node_p_all_title" "$node_p_all_desc" "node $node_p_all_script $folder/" $full_dataset_repeat "node $node_p_all_script $folder/ $subset_prefix" $subset_repeat

# Node.js (async/await)
node_async_title="Node.js \`fsPromises.readFile\` (async/await)"
node_async_desc="Run in sequential order with async/await."
node_async_script="node/tstlp-async-await.mjs"
reportForCommand "$node_async_title" "$node_async_desc" "node $node_async_script $folder/" $full_dataset_repeat "node $node_async_script $folder/ $subset_prefix" $subset_repeat

# Node.js (parallel)
node_parallel_title="Node.js \`fsPromises.readFile\` (parallel)"
node_parallel_desc="Run parallel but ensure sequential output."
node_parallel_script="node/tstlp-parallel.mjs"
reportForCommand "$node_parallel_title" "$node_parallel_desc" "node $node_parallel_script $folder/" $full_dataset_repeat "node $node_parallel_script $folder/ $subset_prefix" $subset_repeat

# Node.js (worker threads)
node_workers_title="Node.js with worker threads"
node_workers_desc="Run parallel and split up workload to worker threads"
node_workers_script="node/tstlp-worker-threads.mjs"
reportForCommand "$node_workers_title" "$node_workers_desc" "node $node_workers_script $folder/" $full_dataset_repeat "node $node_workers_script $folder/ $subset_prefix" $subset_repeat

# Go (sync)
go_sync_title="Go (sync)"
go_sync_desc="Run sequentially with a very basic go program"
go_sync_script="go/tstlp-sync.go"
reportForCommand "$go_sync_title" "$go_sync_desc" "go run $go_sync_script $folder/" $full_dataset_repeat "go run $go_sync_script $folder/ $subset_prefix" $subset_repeat

# Go (parallel)
go_parallel_title="Go (parallel)"
go_parallel_desc="Run parallel with very basic go routines"
go_parallel_script="go/tstlp-parallel.go"
reportForCommand "$go_parallel_title" "$go_parallel_desc" "go run $go_parallel_script $folder/" $full_dataset_repeat "go run $go_parallel_script $folder/ $subset_prefix" $subset_repeat
