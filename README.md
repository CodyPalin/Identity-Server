

**************************************************
CS 455/555: Distributed Systems
Project 2: Identity Server
Team 5
    Cody Palin, Omar Gonzalez
Spring 2020
**************************************************

** Files **
    In the Identity-Server Folder: there is the two attached libraries, the README.md and the makefile. Then there are two folders: client, and server
        In Folder Client: 
            IdClient class: What the users will use to connect to the IdServer and connect to their accounts. Also conducts queries and interacts with the  
                            server.
        
        In Folder Server:
            Identity interface: The interface class that has the required methods for IdServer to function properly.
            IdServer class: The class that is used to create, and run the server and handles requests that it is given by IdClient.
------------------------------------------------------------------------------------------------------------------------------------------------------------

** Building and Running **
    Building:
        1. Enter the Identity Server Directory and find the build.xml file from ant.
        2. do ant -d build
    Running:
        3. To run the server do java IdServer
        4. Then to run a client to see if IdServer works, do java IdClient
        5. in IdClient Terminal do java IdClient --(s)erver localhost (Any Query Commands along with their arguments, some arguments are 
                                                                                        create, lookup, reverselookup, modify, and delete, if the argument is wrong, a message will occur that will give the correct parameters to perform the query)

** Testing ** 
We tested in a simple manner. Essentially whenever a major change was made to the code or pushing and pulling. We ran the program on eclipse then ran IdServer and IdClient to see if the two interacted with new changes. Then we attempted to do different queries or do incorrect arguments in the IdClient terminal and debugged accordingly if it went incorrectly.

** Observations **
It was a struggle to try and start since essentially it was starting from scratch. However there was the resources we went over in class along with the examples in the CS455 repo that gave us the start we needed. 
The interaction with client and server was a little difficult as it was our first time interacting with the RMI library, as the Example in CS455 in the used getRegistry, but that involved starting up the registry outside the java project but createRegistry solved a lot of the issues regarding that. 

The commandline parser library that's been imported was very useful in making the arguments and the code much cleaner to parse out arguments into smaller ones. And saved a lot of time.

Omar: Started with the initial skeleton of the code framework and attempted to fit it towards the assignment, did the javadocs, and README.md
Cody: Fit the code towards the assignment, did a large portion of the rmi interactions with IdClient and IdServer.

Video: https://youtu.be/_HAs1Rt6Tkc

** Misc and Code example **
example code: https://github.com/BoiseState/CS455-resources/tree/master/examples/rmi/ex2-SquareServer
to import class files:
https://stackoverflow.com/questions/3280353/how-to-import-a-jar-in-eclipse
import the two .jar files using this method