#!/bin/bash
java -jar server.jar -l levels/FOMAsimple1.lvl -c 'java -classpath bin dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n"'
