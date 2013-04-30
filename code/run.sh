#!/bin/bash
if [ $# == '1' ]
then
    if [[ "$1" = "debug" ]]
    then
        java -jar server.jar -l levels/FOMAsimple1.lvl -c 'java -classpath bin -Xdebug -Xrunjdwp:transport=dt_socket,server=n,address=8000 dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
    else
    	java -jar server.jar -l levels/FOMAsimple1.lvl -c 'java -classpath bin dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
    fi
else
	java -jar server.jar -l levels/FOMAsimple1.lvl -c 'java -classpath bin dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n" '
fi
