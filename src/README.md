

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

------------------------------------------------------------------------------------------------------------------------------------------------------------

** Building and Running **
    Building:
        1. First Enter into the terminal and head to the docker directory
        2. run either the serversetup.sh using the command
                <runcommand> serversetup.sh <numberofservers>
            when using a debian based linux system 
        2a. or use centosserversetup.sh using
                <runcommand> centosserversetup.sh <numberofservers>
        3. Next go into the main directory that holds the build.xml file and build it.
            For this step we have ant installed and ran the command 
                ant
            into the kernel and the project will build
    Running:
        4. in IdClient Terminal do java -cp commons-cli-1.4.jar:.: client.IdClient --(s)erver idserver# [--numport <ipaddress>] (Any Query Commands along 
                                                                                        with their arguments, some arguments are 
                                                                                        create, lookup, reverselookup, modify, delete, and get if the argument is wrong, a message will occur that will give the correct parameters to perform the query)

** Testing ** 
We began by first testing if the serversetup.sh functioned in a linux based system, and then we decided to test it on another linux based os centos, however the first .sh file did not work because centos doesn't have the same kind of emulation console as debian, so we had to search for an alternative
emulation system along with adjusting the syntax to make it function properly.

** Observations **
It's understandable why the paper plan was due before the project as this project is difficult to picture. Especially since there has not been too many physical examples since there are so many different implementations to handle fault tolerance, multiple servers interacting and the ability for the client to interact with the server even though it is down. There's a lot of small moving pieces that make it difficult to imagine. Not to mention that the beginning steps of making a setupserver file so multiple servers could be set up easily was a somewhat tricky task due to the fact that not even linux distributions share the same kind of files or use the same kind of executables/libraries. We finally got a better grip on how to use ant, making building the project much simpler and nicer.

Omar: Tested using CentOs operating system using a vm do determine if serversetup.sh functioned properly then modified serversetup.sh into a            
      centosserversetup.sh. Tested building and running commands on client.
Cody: Restructured the code, made the dockerfile along with the initial serversetup.sh file. Modified the server files to create an ip list of the current servers

Video: N/A yet

** Misc and Code example **
example code: https://github.com/BoiseState/CS455-resources/tree/master/examples/rmi/ex2-SquareServer
to import class files:
https://stackoverflow.com/questions/3280353/how-to-import-a-jar-in-eclipse
import the two .jar files using this method