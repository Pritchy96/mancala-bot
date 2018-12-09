import subprocess

args = ['java', '-jar', '../../ManKalah.jar',
        'java -jar ../resources/example-jars/JimmyPlayer.jar',
        'java -jar ../target/mancalaBot-1.0-SNAPSHOT-jar-with-dependencies.jar']

p = subprocess.Popen(args)

output = p.communicate()

print(output)
