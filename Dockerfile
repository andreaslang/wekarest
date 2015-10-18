# Dockerfile to build andreas/wekarest

FROM ubuntu:14.04

MAINTAINER Andreas Lang andreas.al.lan@gmail.com

ENV TERM=xterm
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y git mongodb-server openjdk-7-jdk unzip wget

RUN mkdir -p /opt/gradle
WORKDIR /opt/gradle
RUN wget https://services.gradle.org/distributions/gradle-2.7-bin.zip
RUN unzip gradle-2.7-bin.zip
ENV GRADLE_HOME=/opt/gradle/gradle-2.7
ENV PATH=$PATH:$GRADLE_HOME/bin

RUN git clone https://github.com/andreaslang/content-extensions.git /tmp/content-extensions
WORKDIR /tmp/content-extensions
RUN gradle install

RUN mkdir -p /srv/wekarest/data
WORKDIR /srv/wekarest
ADD src/ src/
ADD build.gradle build.gradle

CMD mongod --dbpath /srv/wekarest/data & gradle run