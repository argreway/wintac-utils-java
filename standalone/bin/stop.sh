#!/bin/bash

waitForProcessEnd() {
  pidKilled=$1
  commandName=$2
  processedAt=`date +%s`
  logout=/var/log/uila/dump_$$.out
  MAX_WAIT=30

  while kill -0 ${pidKilled} > /dev/null 2>&1;
  do
     echo -n "."
     sleep 1;
     currentTime=`date +%s`
     timeDiff=$(( ${currentTime} - ${processedAt} ))

     # if process persists more than $MAX_WAIT kill-kill it
     if [ ${timeDiff} -gt ${MAX_WAIT} ]
     then
       break;
     fi
  done

  # process still there : kill -9
  if kill -0 ${pidKilled} > /dev/null 2>&1;
  then
    echo -n "Force stopping ${commandName} with kill -9 ${pidKilled}"
    ${JAVA_HOME}/bin/jstack -l ${pidKilled} > ${logout} 2>&1
    kill -9 ${pidKilled} > /dev/null 2>&1
  fi
  # Add a CR after we're done w/ dots.
  echo
}

pid=(`ps -ef | grep SentryMain | grep -v grep | awk -F  " " '{print $2}'`)

if [ ! -z "$pid" ]
then
  for i in "${pid[@]}"
  do
    echo -n "Shutting down the SentryMain Application [$i] "
    kill $i
    waitForProcessEnd ${i} SentryMain
  done
else
  echo "Server not running."
fi