FROM centos:7
RUN yum -y install java
RUN yum -y install java-devel
RUN yum -y install git
RUN yum -y install ant
#RUN yum -y install gcc
#RUN yum -y install make
RUN git clone https://github.com/CodyPalin/Identity-Server.git
RUN (cd ./Identity-Server/ && ant)
CMD (cd ./Identity-Server/bin && java -cp commons-cli-1.4.jar:.: server.IdServer --verbose)