#!/bin/bash

JAVA=${JAVA:-java}
JAVA_OPTS=${JAVA_OPTS:-"-Xms128m -Xmx1536m -XX:+UseG1GC -Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8"}

err=1
until [ $err = 0 ];
do
	[ -d log/backup ] || mkdir -p log/backup
	[ -f log/console.log ] && mv log/console.log "log/backup/`date +%Y-%m-%d_%H-%M-%S`_console.log"
	"$JAVA" $JAVA_OPTS -ea -javaagent:./libs/al-commons.jar -cp "./libs/*:AL-Game.jar" com.aionemu.gameserver.GameServer > log/console.log 2>&1 &
	gspid=$!
	echo ${gspid} > gameserver.pid
	wait ${gspid}
	err=$?
	sleep 10
done
