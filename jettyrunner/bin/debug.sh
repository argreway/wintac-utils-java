#!/bin/bash
current=`pwd`

source /etc/profile

if [ -z ${JAVA_HOME} ]
then
  echo "Must set JAVA_HOME."
  return 1
fi

if [ -f ${projectBaseDir} ]
then
  cd ${projectBaseDir}
fi

cmd="${JAVA_HOME}/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Log4jLogger -Dlog4j.configuration=file:${current}/conf/log4j.properties -jar ${current}/thirdparty/jetty-runner-9.4.12.RC0.jar --port 8080 ${current}/webapp-1.0-SNAPSHOT.war"

echo $cmd

$cmd

if [ $? -ne 0 ]
then
  echo "Failed to start SentryWebapp!"
  cd ${current}
  exit 1
fi

echo "SentryWebapp started [$!] ..."
cd ${current}
