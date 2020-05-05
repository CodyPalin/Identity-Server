#!/bin/bash
read -p "Enter Password: " -s pass
if [ -z ${2+x} ] || [ "$2" != "nocache" ]
    then echo $pass | sudo -S docker build -t identityimage:1.1 .
    else echo $pass | sudo -S docker build --no-cache=true -t identityimage:1.1 .
fi
echo "Launching $1 servers..."
for ((i=1; i <= $1; ++i))
do 
    gnome-terminal gnome-terminal -x bash -c "echo $pass | sudo -S docker run --name="idserver${i}" --hostname="idserver${i}" identityimage:1.1 ; exec bash"
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