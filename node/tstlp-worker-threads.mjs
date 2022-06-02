import fs from "fs";
import { countStringPerLine } from "./functions/count-string-per-line.mjs";
import { fileNamesFromArgs } from "./functions/file-names-from-args.mjs";
import { STUCK_THREADS_DETECTED_LOG_MESSAGE } from "./functions/stuck-threads.mjs";
import { WorkerThreadPool } from "./functions/worker-thread-pool.mjs";
import { isMainThread } from "worker_threads";
import { cpus } from "os";

const fsPromises = fs.promises;

if (isMainThread) {
    const {folderName, fileNames} = fileNamesFromArgs();

    const workerThreadPool = new WorkerThreadPool({
        numberOfWorkers: cpus().length - 1,
        moduleFilename: process.argv[1],
        endAfterMessageCount: fileNames.length
    });

    workerThreadPool.onMessageOrderer((result) => {
        console.log(`${result.filename}:${result.stuckThreads}`);
    });

    fileNames.forEach(filename => {
        workerThreadPool.dispatchMessage({
            filename: `${folderName}${filename}`
        })
    })

} else {
    WorkerThreadPool.onMessageToWorker((message, done) => {
        return fsPromises.readFile(message.filename, 'utf-8').then(content => {
            done({
                filename: message.filename,
                stuckThreads: countStringPerLine(content, STUCK_THREADS_DETECTED_LOG_MESSAGE)
            });
        });
    });
}
