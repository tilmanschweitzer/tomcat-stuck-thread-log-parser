import { Worker, parentPort, isMainThread } from "worker_threads";

class WorkerThreadPool {
    workers = []
    messageInputIndex = 0
    messageReturnedIndex = 0
    messageResults = [] // TODO: Rework to avoid memory leaks

    constructor({numberOfWorkers, moduleFilename, endAfterMessageCount}) {
        this._initWorkers(numberOfWorkers, moduleFilename);
        this.endAfterMessageCount = endAfterMessageCount;
    }

    static onMessageToWorker(callback) {
        if (isMainThread) {
            throw new Error("Only use this function in worker thread, not in main thread.")
        }

        parentPort.on('message', (internalRequestMessage) => {
            const { messageToProcess, messageIndex} = internalRequestMessage;
            callback(messageToProcess, (result) => {
                // send back all information to allow processing the results in order
                parentPort.postMessage({
                    messageToProcess,
                    messageIndex,
                    result,
                });
            });
        });
    }

    _initWorkers(numberOfWorkers, moduleFilename) {
        if (this.workers.length > 0) {
            throw new Error("Worker threads already initialized");
        }

        for (let i = 0; i < numberOfWorkers; i++) {
            this.workers.push(new _InternalWorker(moduleFilename))
        }
    }

    _terminateAllWorkers() {
        this.workers.forEach(worker => worker.terminate());
    }

    onMessageOrderer(callback) {
        this.workers.forEach(worker => {
            worker.onMessage((internalResultMessage) => {
                const { result, messageIndex } = internalResultMessage;

                this.messageResults[messageIndex] = result;

                while (this.messageResults[this.messageReturnedIndex]) {
                    const latestResultInOrder = this.messageResults[this.messageReturnedIndex];
                    callback(latestResultInOrder);
                    this.messageReturnedIndex++;
                }

                if (this.messageReturnedIndex >= this.endAfterMessageCount) {
                    this._terminateAllWorkers()
                }
            });
        });
    }

    dispatchMessage(messageToProcess) {
        const workerForMessage = this._nextWorker();
        const internalMessage = {
            messageToProcess,
            messageIndex: this.messageInputIndex++
        }
        workerForMessage.postMessage(internalMessage);
    }

    _nextWorker() {
        return this.workers[this._nextWorkerIndex()];
    }

    _nextWorkerIndex() {
        // Use round-robin scheduling
        return this.messageInputIndex % this.workers.length;
    }
}

class _InternalWorker {
    static latestWorkerId = 0;
    onMessageHandlerRegistered = false;

    constructor(moduleFilename) {
        this.id = _InternalWorker.latestWorkerId++;
        this.worker = new Worker(moduleFilename, {
            workerData: {
                id: this.id
            }
        });
        this.processedMessages = 0;
    }

    onMessage(callback) {
        if (this.onMessageHandlerRegistered) {
            throw new Error("Thread pool allows only one message handler per pool instance to work properly");
        }
        this.onMessageHandlerRegistered = true;
        this.worker.on("message", callback);
    }

    postMessage(message) {
        this.worker.postMessage(message);
    }

    terminate() {
        this.worker.terminate();
    }
}


export { WorkerThreadPool }
