#!/bin/sh

nohup java -Dfile.encoding=UTF-8 -jar print-server-1.0-SNAPSHOT.jar -c test-routes.xml -w &
