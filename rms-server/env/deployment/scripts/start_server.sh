#!/bin/bash -x
if [ -f /home/ec2-user/private/rms-secret ]; then
    . /home/ec2-user/private/rms-secret
fi

cd /home/ec2-user/rmsServiceApp
export DEBUG_SLEEP_ENABLE=true
export DEBUG_SLEEP_TIME=200
export JWT_FILTER_ENABLE=true

echo "[rmsServiceApp]STARTING..."
java -Dlogback.configurationFile=./logback-production.xml -jar rms-server.jar > /dev/null 2>&1 < /dev/null &

exit $?
