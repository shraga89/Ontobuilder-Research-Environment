#!/usr/bin/python2.7
import sys
import re


if __name__ == "__main__":
    filename = sys.argv[1]
    f = open(filename)
    for line in f:
        line = line.split()[1:]
        line = " ".join(line)
        regex = re.compile("[\(\[][\w\W]*[\)\]]")
        line = regex.sub('', line)
        line = line.replace('-','')
        line = line.lower()
        line = " ".join(line.split())
        line = line.decode('utf8').encode('ascii', 'replace')
        print line