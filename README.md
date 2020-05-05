

**************************************************
CS 455/555: Distributed Systems
Project 3: Identity Server Multiple Servers
Team 5
    Cody Palin, Omar Gonzalez
Spring 2020
**************************************************

** Files **
    Identity-Server
        bin
            client and server folders hold the .class files along with respective .jar library files
        docker
            Holds the docker file along with 2 serversetup sh files.
            Dockerfile
            centosserversetup.sh
            serversetup.sh
        src
            client
                Idclient.java
            server
                Identity.java
                IdServer.java
                ServerCommunication.java
                SetupCommunication.java
            Libraries
        build.xml
        Makefile
        serverinit.sh

------------------------------------------------------------------------------------------------------------------------------------------------------------

** Building and Running **
    Building:
        1. To start the servers run either the serversetup.sh using the command
                $(cd docker && ./serversetup.sh \[numservers\] )
            when using a debian based linux system 
        1a. or use centosserversetup.sh using
                $(cd docker && ./centosserversetup.sh \[numservers\] )
        2. Next initialize the servers with:
        $./serverinit.sh
            This will send docker/IPlist.txt to the servers, they are now ready for client commands.
        3. make clean && make
    Running:
        4. in IdClient Terminal do java -cp commons-cli-1.4.jar:.: client.IdClient --(s)erver [file.txt | ip] (Any Query Commands along with their arguments, some arguments are (c)reate, (l)ookup, (r)everselookup, (m)odify, (d)elete, and (g)et if the argument is wrong, a message will occur that will give the correct parameters to perform the query) some commands (create, modify, and delete) require a password. --(p)assword \[password\]

** Testing ** 
We began by first testing if the serversetup.sh functioned in a linux based system, and then we decided to test it on another linux based os centos, however the first .sh file did not work because centos doesn't have the same kind of emulation console as debian, so we had to search for an alternative
emulation system along with adjusting the syntax to make it function properly.

See samples.txt for examples testing of the servers.

Modified the code to have print statements when events were happening to give insight into when certain things were happening and on which servers they were happening. This helped to develop an understanding of where in the code something was goin wrong and why.


** Observations **
Omar:
It's understandable why the paper plan was due before the project as this project is difficult to picture. Especially since there has not been too many physical examples since there are so many different implementations to handle fault tolerance, multiple servers interacting and the ability for the client to interact with the server even though it is down. There's a lot of small moving pieces that make it difficult to imagine. Not to mention that the beginning steps of making a setupserver file so multiple servers could be set up easily was a somewhat tricky task due to the fact that not even linux distributions share the same kind of files or use the same kind of executables/libraries. We finally got a better grip on how to use ant, making building the project much simpler and nicer.
Cody:
After working on this project I can see that it is very complicated to create a distributed server that will be error free, I know of a couple situations in which the server would throw an error from client commands at the wrong time before a specific server is ready to accept them, or a server dying right at the wrong moment. Distributed computing is much more complex than I had anticipated, but it is just as logical as any other program, there are just a lot more factors to look into.

** Contribution **

Omar: Tested using CentOs operating system using a vm do determine if serversetup.sh functioned properly then modified serversetup.sh into a centosserversetup.sh. Tested building and running commands on client.

Cody: Added savestate functionality to IdServer. Created Dockerfile. Built serversetup.sh so we could launch multiple servers easily to make testing easier and save time. Created SetupCommunication.java for passing the IPs into each of the servers then running the first election. Made serverinit.sh for easy calling of SetupCommunication.java in the default case of passing in the IPlist.txt. Created and implemented ServerCommunication interface for server-server messages. Tested IdServer to make sure consistency and coordination worked without too many flaws. Modified IdServer so that it could handle .txt input as well as just an IP/hostname. Created Makefile.

Video: https://youtu.be/M3MCR1Rvlg0

