#!/bin/bash

source /etc/profile

current=`pwd`
projectBaseDir=/opt/sentry/SentryWebapp

if [ -z ${JAVA_HOME} ]
then
  echo "Must set JAVA_HOME."
  return 1
fi

if [ -f ${projectBaseDir} ]
then
  cd ${projectBaseDir}
fi

cmd="${JAVA_HOME}/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Log4jLogger -Dlog4j.configuration=file:conf/log4j.properties -jar thirdparty/jetty-runner-9.4.12.RC0.jar --port 8080 webapp-1.0-SNAPSHOT.war"

echo $cmd $@

$cmd $@ > /var/log/sentry/webapp-out.log 2>&1 &

if [ $? -ne 0 ]
then
  echo "Failed to start SentryWebapp!"
  cd ${current}
  exit 1
fi

echo "SentryWebapp started [$!] ..."
cd ${current}
