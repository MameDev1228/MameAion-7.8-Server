#!/bin/sh

JAVA=${JAVA:-java}
JAVA_OPTS=${JAVA_OPTS:-"-Xms64m -Xmx256m -XX:+UseG1GC -Dfile.encoding=UTF-8 -DconsoleEncoding=UTF-8"}

err=1
until [ $err = 0 ];
do
	"$JAVA" $JAVA_OPTS -ea -cp "./libs/*:AL-Login.jar" com.aionemu.loginserver.LoginServer &
	lspid=$!
	echo ${lspid} > loginserver.pid
	echo "LoginServer started!"
	wait ${lspid}
	err=$?
	sleep 10
done
