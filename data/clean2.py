#!/usr/bin/python2.7
import sys
import re


if __name__ == "__main__":
    filename = sys.argv[1]
    f = open(filename)
    for line in f:
        line = " ".join(line.split(':')[-2:-1])
        regex = re.compile("[\(\[][\w\W]*[\)\]]")
        line = regex.sub('', line)
        if ')' in line:
            line = " ".join(line.split(')')[1:])
        # line = line.replace('-','')
        line = line.lower()
        line = " ".join(line.split())
        line = line.decode('utf8').encode('ascii', 'replace')
        print line