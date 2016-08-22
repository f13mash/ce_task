#!/bin/bash
projdir=`pwd`
apt-get -y update
apt-get install -y wget unzip default-jdk redis-tools
wget http://www.scala-lang.org/files/archive/scala-2.11.6.deb -o nul
dpkg -i scala-2.11.6.deb
apt-get -y update
apt-get -y install scala
cd /opt
wget http://downloads.typesafe.com/typesafe-activator/1.3.2/typesafe-activator-1.3.2-minimal.zip
unzip typesafe-activator-1.3.2-minimal.zip
mv activator-1.3.2-minimal activator
ln -s /opt/activator/activator /usr/local/bin/activator

cd $projdir
activator compile
