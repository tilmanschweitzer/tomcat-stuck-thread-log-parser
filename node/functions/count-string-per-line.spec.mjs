import {countStringPerLine} from "./count-string-per-line.mjs";
import {STUCK_THREADS_DETECTED_LOG_MESSAGE} from "./stuck-threads.mjs";

describe("countStringPerLine", function () {
    it("finds nothings in empty string", function () {
        expect(countStringPerLine("", STUCK_THREADS_DETECTED_LOG_MESSAGE)).toBe(0);
    });

    it("finds one occurrence in multi line string", function () {
        expect(countStringPerLine("empty line\nnotifyStuckThreadDetected", STUCK_THREADS_DETECTED_LOG_MESSAGE)).toBe(1);
    });

    it("finds multiple occurrences in multi line string", function () {
        const multiLineString = `empty line
            notifyStuckThreadDetected (1)
            empty line
            2022-01-01 notifyStuckThreadDetected (2)
            empty line
            notifyStuckThreadDetected (3)`;

        expect(countStringPerLine(multiLineString, STUCK_THREADS_DETECTED_LOG_MESSAGE)).toBe(3);
    });

    it("counts multiple occurrences in one line just once", function () {
        const multiLineString = `empty line
            notifyStuckThreadDetected notifyStuckThreadDetected notifyStuckThreadDetected (1)
            empty line
            2022-01-01 notifyStuckThreadDetected (2)`;

        expect(countStringPerLine(multiLineString, STUCK_THREADS_DETECTED_LOG_MESSAGE)).toBe(2);
    });
});
