import subprocess
import math
import random

nGenerations = 10
nInInitialGeneration = 2

winDict = {}
ourRunArg = 'java -jar ../../target/mancalaBot-1.0-SNAPSHOT-jar-with-dependencies.jar'

def deviate(w):
    dev1 = random.triangular(-0.3, 0.3, 0.0)
    dev2 = random.triangular(-0.3, 0.3, 0.0)
    dev3 = random.triangular(-0.3, 0.3, 0.0)
    dev4 = random.triangular(-0.3, 0.3, 0.0)
    dev5 = random.triangular(-0.3, 0.3, 0.0)
    temp = w.split(" ")
    wNew = " " + str(float(temp[1]) + dev1) + " " + str(float(temp[2]) + dev2) + " " + str(float(temp[3]) + dev3) + " " + str(float(temp[4]) + dev4) + " " + str(float(temp[5]) + dev5)
    return wNew

def deviate_one(w):
    dev1 = random.triangular(-0.1, 0.1, 0.0)
    return w + dev1



def play(w, wPrime):
    print("playing")
    args = ['java', '-jar', '../../../ManKalah.jar',
            ourRunArg + w,
            ourRunArg + wPrime]
    p = subprocess.run(args, capture_output=True)
    out = str(p.stdout).split("WINNER: ")
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
        winDict[wPrime] = winDict[wPrime] + 3


for x in range(0, nInInitialGeneration):
    h1 = str(random.triangular(0.4, 0.55, 0.5))
    h2 = str(random.triangular(0.4, 0.55, 0.5))
    h3 = str(random.triangular(0.4, 0.55, 0.5))
    h4 = str(random.triangular(0.4, 0.55, 0.5))
    h5 = str(random.triangular(0.4, 0.55, 0.5))
    weight = " " + h1 + " " + h2 + " " + h3 + " " + h4 + " " + h5
    winDict[weight] = 0


for x in range(1, nGenerations):
    print("new generation", x)
    for key, value in winDict.items():
        for keyPrime, valuePrime in winDict.items():
            if key is not keyPrime:
                play(key, keyPrime)

    winDict = dict([(k, winDict[k]) for k in sorted(winDict, key=winDict.get, reverse=True)])
    print(winDict)
    print("end of generation: ", x)
    twentyPercent = int(math.ceil(len(winDict) * 0.5))

    remove = []
    count = 0
    for k,v in reversed(sorted(winDict.items())):
        if count < twentyPercent:
            remove.append(k)
            count = count + 1

    for rmk in remove:
        del winDict[rmk]

    if x is not nGenerations - 1:
        newWinDict = {}
        for k, v in winDict.items():
            newWinDict[k] = 0
            newWinDict[deviate(k)] = 0

        winDict = newWinDict


print(winDict)
# now just get the 'best' of the resulting
winDict = dict([(k, winDict[k]) for k in sorted(winDict, key=winDict.get, reverse=True)])
for k, v in winDict.items():
    print("Absolute Best: " + str(k) + " with " + str(v) + " wins")
    break

