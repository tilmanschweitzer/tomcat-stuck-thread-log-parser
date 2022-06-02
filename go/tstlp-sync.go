package main

import (
    "bufio"
    "fmt"
    "log"
    "os"
    "strings"
)

func main() {
    filenames := filenamesFromArgs()

    for _, filename := range filenames {
        countedStuckThread := countStuckThreadsInFile(filename);
        fmt.Printf("%s:%d\n", filename, countedStuckThread)
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

    return counter
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
