function countStringPerLine(content, searchString) {
    const lines = content.split(/\r?\n/);
    let occurrences = 0;
    lines.forEach(line => {
        if (line.indexOf(searchString) >= 0) {
            occurrences++;
        }
    });
    return occurrences;
}


export {countStringPerLine}
