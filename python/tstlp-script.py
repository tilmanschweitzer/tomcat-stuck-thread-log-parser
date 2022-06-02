import sys
import os

folder = sys.argv[1]

filenames = []

with os.scandir(folder) as file_iterator:
    for dir_entry in file_iterator:
        filename = dir_entry.name
        filenames.append(filename)


filenames.sort()

for filename in filenames:
    counter = 0
    with open(folder + filename) as file:
        line = file.readline()

        while line:
            if "notifyStuckThreadD" in line:
                counter += 1
            line = file.readline()

        print(filename + ":" + str(counter))
