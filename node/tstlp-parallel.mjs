import fs from "fs";
import {countStringPerLine} from "./functions/count-string-per-line.mjs";
import {fileNamesFromArgs} from "./functions/file-names-from-args.mjs";
import {STUCK_THREADS_DETECTED_LOG_MESSAGE} from "./functions/stuck-threads.mjs";

const fsPromises = fs.promises;
const {folderName, fileNames} = fileNamesFromArgs();

const readFilePromises = fileNames.map(filename => {
    return fsPromises.readFile(`${folderName}/${filename}`, 'utf-8').then(file => {
        const stuckThreads = countStringPerLine(file, STUCK_THREADS_DETECTED_LOG_MESSAGE);
        return {
            filename,
            stuckThreads
        }
    });
});

function resolvePromisesInSequence(promises, callback) {
    if (promises.length === 0) {
        return;
    }
    promises[0].then(callback)
        .then(() =>  {
            resolvePromisesInSequence(promises.slice(1), callback)
        });
}

resolvePromisesInSequence(readFilePromises, (result) => {
    console.log(`${result.filename}:${result.stuckThreads}`);
});
