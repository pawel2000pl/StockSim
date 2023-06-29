#!/bin/bash

ORG_PWD="$PWD"
cd /frontend
python3 server.py &
FRONTEND_PID=$!

cd "$ORG_PWD"
catalina.sh run 

kill $FRONTEND_PID
wait $FRONTEND_PID
