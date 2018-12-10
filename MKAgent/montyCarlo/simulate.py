import subprocess
from collections import OrderedDict

winDict = {}


def play(w, wPrime):
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
    weight = " 0.1 0.2 0.3 0.5"
    wAll.append(weight)
    winDict[weight] = 0

for x in range(1, 10):
    for w in wAll:
        for wPrime in wAll:
            if w is not wPrime:
                play(w, wPrime)
                play(wPrime, w)
    winDict = OrderedDict(sorted(winDict.items()))
    twentyPercent = len(winDict) * 0.2
    count = 0
    for key, value in winDict.items():
        if count <= twentyPercent:
            wAll.remove(key)
        else:
            break
