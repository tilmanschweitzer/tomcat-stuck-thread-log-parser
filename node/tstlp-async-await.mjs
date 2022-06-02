import fs from "fs";
import {countStringPerLine} from "./functions/count-string-per-line.mjs";
import {fileNamesFromArgs} from "./functions/file-names-from-args.mjs";
import {STUCK_THREADS_DETECTED_LOG_MESSAGE} from "./functions/stuck-threads.mjs";

const fsPromises = fs.promises;
const {folderName, fileNames} = fileNamesFromArgs();

async function readFiles(filenames) {
    for (const filename of filenames) {
        const file = await fsPromises.readFile(`${folderName}/${filename}`, 'utf-8');
        const stuckThreads = countStringPerLine(file, STUCK_THREADS_DETECTED_LOG_MESSAGE);
        console.log(`${filename}:${stuckThreads}`);
    }
}

await readFiles(fileNames);
