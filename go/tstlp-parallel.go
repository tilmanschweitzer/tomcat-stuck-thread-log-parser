package main

import (
    "bufio"
    "fmt"
    "log"
    "os"
    "strings"
)

type Result struct {
    filename string
    index int
    stuckThreads int
}

func main() {
    filenames := filenamesFromArgs()

    ch := make(chan Result, len(filenames))

    for i, filename := range filenames {
        go countStuckThreadsInFileAsync(filename, i, ch)
    }

    outputIndex := 0
    results :=  make([]Result, len(filenames) + 1)

    for result := range ch {
        results[result.index] = result

        for results[outputIndex].filename != "" {
            outputResult := results[outputIndex];
            fmt.Printf("%s:%d\n", outputResult.filename, outputResult.stuckThreads)
            outputIndex++

            if (outputIndex >= len(filenames)) {
                close(ch)
            }
        }
    }
}

func countStuckThreadsInFile(filename string) (int) {
    file, err := os.Open(filename)
    if err != nil {
        log.Fatal(err)
    }
    defer file.Close()

    counter := 0

    scanner := bufio.NewScanner(file)

    for scanner.Scan() {
        if strings.Contains(scanner.Text(), "notifyStuckThreadDetected") {
            counter++
        }
    }

    if err := scanner.Err(); err != nil {
        log.Fatal(err)
    }

    file.Close()
    return counter
}

func countStuckThreadsInFileAsync(filename string, index int, ch chan Result) {
    countedStuckThreads := countStuckThreadsInFile(filename)
    ch <- Result{filename, index, countedStuckThreads}
}

func filenamesFromArgs() ([]string) {
    args := os.Args[1:]

    if len(args) < 1 {
        fmt.Println("Error: No folder given")
        os.Exit(1)
    }

    folder := args[0]
    prefix := valueAtIndexOrDefault(args, 1, "")

    files, err := os.ReadDir(folder)
    if err != nil {
        log.Fatal(err)
    }

    var filenames []string

    for _, file := range files {
        if strings.Contains(file.Name(), prefix) {
            filenames = append(filenames, folder + "/" + file.Name())
        }
    }

    return filenames
}

func valueAtIndexOrDefault(args []string, index int, defaultValue string) (string) {
    if len(args) > index {
        return args[index]
    }
    return defaultValue
}
