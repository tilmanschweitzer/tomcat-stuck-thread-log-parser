import sys
import os

def filenames_from_folder(path):
    filenames = []

    with os.scandir(path) as file_iterator:
        for dir_entry in file_iterator:
            filename = dir_entry.name
            filenames.append(filename)

    filenames.sort()

    return filenames

def count_string_in_file(path, s):
    counter = 0

    with open(path) as file:
        line = file.readline()

        while line:
            if s in line:
                counter += 1
            line = file.readline()

    return counter


def main():
    folder = sys.argv[1]

    for filename in filenames_from_folder(folder):
        stuckThreadCounter = count_string_in_file(folder + filename, "notifyStuckThreadDetected")
        print(filename + ":" + str(stuckThreadCounter))

main()
