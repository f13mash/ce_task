FROM ubuntu:16.04
MAINTAINER mashf13

ADD ./ /app

RUN apt-get -y update
RUN apt-get install -y wget unzip default-jdk redis-tools
RUN wget http://www.scala-lang.org/files/archive/scala-2.11.6.deb -o nul
RUN dpkg -i scala-2.11.6.deb
RUN apt-get -y update
RUN apt-get -y install scala
WORKDIR /opt
RUN wget http://downloads.typesafe.com/typesafe-activator/1.3.2/typesafe-activator-1.3.2-minimal.zip
RUN unzip typesafe-activator-1.3.2-minimal.zip
RUN mv activator-1.3.2-minimal activator
RUN ln -s /opt/activator/activator /usr/local/sbin/activator

WORKDIR /app
ENTRYPOINT ["/app/script.sh"]

