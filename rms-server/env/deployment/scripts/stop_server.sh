#!/bin/bash

message=`curl http://localhost/mng/stop`

sleep 3s

if [ "$message" = "success" ]; then
  echo "[rmsServiceAppliction]STOP SUCCESS"
  exit 0
else
  echo "[rmsServiceAppliction]STOP ERROR!!"
  exit 1
fi
