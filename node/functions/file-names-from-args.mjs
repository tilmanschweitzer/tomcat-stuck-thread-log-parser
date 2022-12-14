import fs from "fs";

function fileNamesFromArgs() {
    const args = process.argv.slice(2);

    const folderName = replaceTrailingSlash(args[0]);
    const startsWith = args[1] || "";

    if (!fs.existsSync(folderName)) {
        console.error(`Folder ${folderName} does not exists`);
        process.exit(1)
    }

    const fileNames = fs.readdirSync(folderName).filter(filename => filename.startsWith(startsWith));

    if (fileNames.length === 0 && startsWith) {
        console.error(`No files found in ${folderName} that start with '${startsWith}'`);
        process.exit(1)
    }

    return {
        folderName,
        startsWith,
        fileNames
    }
}

function replaceTrailingSlash(folderName) {
    if (!folderName.endsWith("/")) {
        return folderName;
    }
    return folderName.slice(0, folderName.length - 1);
}

export {fileNamesFromArgs}
