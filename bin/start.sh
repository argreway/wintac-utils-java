#!/bin/bash

source /etc/profile
export JAVA_HOME=/usr/java/latest

current=`pwd`
projectBaseDir=/opt/sentry/SentryMain

if [ -z ${JAVA_HOME} ]
then
  echo "Must set JAVA_HOME."
  return 1
fi

cd ${projectBaseDir}

cmd="${JAVA_HOME}/bin/java -Xms1G -Xmx1G -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC -XX:-OmitStackTraceInFastThrow  -XX:+PrintGCCause  -XX:+PrintGCDateStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=4 -XX:GCLogFileSize=4M -Xloggc:/var/log/sentry/restserver-gc.log -Dorg.apache.cxf.Logger=org.apache.cxf.common.logging.Log4jLogger -Dlog4j.configuration=file:conf/log4j.properties -cp ./conf/:sentry-1.0-SNAPSHOT.jar:lib/*:thirdparty/* com.sentryfire.SentryMain -server"

#echo $cmd $@

$cmd $@ > /var/log/sentry/sentry-out.log 2>&1 &

if [ $? -ne 0 ]
then
  echo "Failed to start SentryMain!"
  cd ${current}
  exit 1
fi

echo "SentryMain started [$!] ..."
cd ${current}
