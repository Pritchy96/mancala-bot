import subprocess
from collections import OrderedDict
import math
import random

winDict = {}

def deviate(w):
    dev1 = random.triangular(0, 0.1, 0.05)
    dev2 = random.triangular(0, 0.1, 0.05)
    dev3 = random.triangular(0, 0.1, 0.05)
    temp = w.split(" ")
    wNew = " " + str(float(temp[1]) + dev1) + " " + str(float(temp[2]) + dev2) + " " + str(float(temp[3]) + dev3)
    return wNew


def play(w, wPrime):
    print("playing")
    p = subprocess.run(args, capture_output=True)
    out = str(p.stderr).split("WINNER: ")
    wl = out[1].split("\\n")
    winner = wl[0]
    if w in winner:
        winDict[w] = winDict[w] + 1
    else:
        winDict[wPrime] = winDict[wPrime] + 1


us = "Player 1"
ourRunArg = 'java -jar ../target/mancalaBot-1.0-SNAPSHOT-jar-with-dependencies.jar'

args = ['java', '-jar', '../../ManKalah.jar',
        ourRunArg + " -p1",
        'java -jar ../resources/example-jars/JimmyPlayer.jar']

wAll = []



for x in range(0, 50):
    h1 = str(random.triangular(0, 1, 0.5))
    h2 = str(random.triangular(0, 1, 0.5))
    h3 = str(random.triangular(0, 1, 0.5))
    weight = " " + h1 + " " + h2 + " " + h3
    wAll.append(weight)
    winDict[weight] = 0


for x in range(1, 2):
    for w in wAll:
        for wPrime in wAll:
            if w is not wPrime:
                play(w, wPrime)

    print(winDict)
    winDictSorted = OrderedDict(sorted(winDict.items()))
    twentyPercent = int(math.ceil(len(winDictSorted) * 0.2))
    print(twentyPercent)
    count = 0
    for key, value in winDictSorted.items():
        if count < twentyPercent:
            wAll.remove(key)
        else:
            break
        count = count + 1

    for w in wAll:
        w = deviate(w)

