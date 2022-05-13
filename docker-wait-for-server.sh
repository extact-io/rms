#!/bin/bash

set -e

cmd="$@"

until curl -f "${SERVER_URL}/health" &> /dev/null; do
  >&2 echo "rms-server is not started yet - waiting"
  sleep 1
done

>&2 echo "rms-server is up - executing command"
exec $cmd
