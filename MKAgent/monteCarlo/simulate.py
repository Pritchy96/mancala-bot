import subprocess
from collections import OrderedDict
import math
import random

winDict = {}
ourRunArg = 'java -jar ../target/mancalaBot-1.0-SNAPSHOT-jar-with-dependencies.jar'

def deviate(w):
    dev1 = random.triangular(0, 0.1, 0.05)
    dev2 = random.triangular(0, 0.1, 0.05)
    dev3 = random.triangular(0, 0.1, 0.05)
    temp = w.split(" ")
    print(temp)
    wNew = " " + str(float(temp[1]) + dev1) + " " + str(float(temp[2]) + dev2) + " " + str(float(temp[3]) + dev3)
    return wNew


def play(w, wPrime):
    print("playing")
    args = ['java', '-jar', '../../ManKalah.jar',
            ourRunArg + w,
            ourRunArg + wPrime]
    p = subprocess.run(args, capture_output=True)
    out = str(p.stderr).split("WINNER: ")
    if len(out) is 1:
        out = str(p.stderr).split("DRAW: ")
        winDict[w] = winDict[w] + 1
        winDict[wPrime] = winDict[wPrime] + 1
        return
    wl = out[1].split("\\n")
    winner = wl[0]
    if w in winner:
        winDict[w] = winDict[w] + 3
    else:
        winDict[wPrime] = winDict[wPrime] + 0


us = "Player 1"

wAll = []



for x in range(0, 3):
    h1 = str(random.random())
    h2 = str(random.random())
    h3 = str(random.random())
    weight = " " + h1 + " " + h2 + " " + h3
    wAll.append(weight)
    winDict[weight] = 0


for x in range(1, 5):
    for w in wAll:
        for wPrime in wAll:
            if w is not wPrime:
                play(w, wPrime)

    print(winDict)
    winDictSorted = OrderedDict(sorted(winDict.items()))
    twentyPercent = int(math.ceil(len(winDictSorted) * 0.2))
    for i in range(0, twentyPercent):
        currentMin = min(winDictSorted, key=winDictSorted.get)
        wAll.remove(currentMin)
        del winDict[currentMin]


    max = len(wAll)
    for t in range(0, max):
        newWeight = deviate(wAll[t])
        wAll.append(newWeight)
        winDict[newWeight] = 0

# now just get the 'best' of the resulting
maximum = max(winDict, key=winDict.get)
print("Absolute Best: " + maximum)
