import sys
import os

folder = sys.argv[1].strip("/")

filenames = []

with os.scandir(folder) as file_iterator:
    for dir_entry in file_iterator:
        filename = dir_entry.name
        filenames.append(filename)


filenames.sort()

for filename in filenames:
    counter = 0
    path = folder + "/" + filename
    with open(path) as file:
        line = file.readline()

        while line:
            if "notifyStuckThreadD" in line:
                counter += 1
            line = file.readline()

        print(path + ":" + str(counter))
