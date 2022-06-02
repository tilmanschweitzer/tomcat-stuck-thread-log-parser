import fs from "fs";
import {countStringPerLine} from "./functions/count-string-per-line.mjs";
import {fileNamesFromArgs} from "./functions/file-names-from-args.mjs";
import {STUCK_THREADS_DETECTED_LOG_MESSAGE} from "./functions/stuck-threads.mjs";

const {folderName, fileNames} = fileNamesFromArgs();

fileNames.forEach(filename => {
    const file = fs.readFileSync(`${folderName}/${filename}`, 'utf-8');
    const stuckThreads = countStringPerLine(file, STUCK_THREADS_DETECTED_LOG_MESSAGE)
    console.log(`${filename}:${stuckThreads}`);
});
