#!/bin/bash
LEVEL="levels/FOSAboxesOfHanoi.lvl"
if [ $# == '1' ]
then
    if [[ "$1" = "debug" ]]
    then
        java -jar server.jar -l $LEVEL -c 'java -classpath bin:lib/* -Xdebug -Xrunjdwp:transport=dt_socket,server=n,address=8000 dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
    else
    	java -jar server.jar -l $LEVEL -g -p -c 'java -classpath bin:lib/* dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
    fi
else
	java  -jar server.jar -l $LEVEL -c 'java -classpath lib/*:bin dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
fi
