#!/bin/bash
read -p "Enter Password: " -s pass
echo $pass | sudo -S docker build -t identityimage:1.1 .
echo "Launching $1 servers..."
for ((i=1; i <= $1; ++i))
do 
    x-terminal-emulator -e "echo $pass | sudo -S docker run --name="idserver${i}" --hostname="idserver${i}" timeserverimage:1.1"
done
sleep 8
echo "Retrieving IPs..."
rm -f IPlist.txt
for ((i=1; i<= $1; ++i))
do 
    sudo docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' idserver$i >> IPlist.txt
done
echo "IPs saved."
echo "done"