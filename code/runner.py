import os
import glob
import subprocess
import argparse
import re
import datetime
import time

parser = argparse.ArgumentParser(description='Execute all AI levels')
parser.add_argument('--s', dest='fosa', action='store_true')
parser.add_argument('--m', dest='foma', action='store_true')
parser.add_argument('--p', dest='poma', action='store_true')
parser.add_argument('--a', dest='all', action='store_true')
parser.add_argument('--d', dest='debug', action='store_true')
parser.add_argument('--g', dest='graph', action='store_true')
parser.set_defaults(debug=False, all=False, fosa=False, foma=False, poma=False)

args = parser.parse_args()

all_levels = [file for file in glob.glob('levels/*')]
levels = []

# filter levels
if not args.all:
    if args.fosa:
        levels.extend([file for file in all_levels if re.match(r'levels\\FOSA.*', file)])
    if args.foma:
        levels.extend([file for file in all_levels if re.match(r'levels\\FOMA.*', file)])
    if args.poma:
        levels.extend([file for file in all_levels if re.match(r'levels\\POMA.*', file)])
else:
    levels = all_levels
    
if not os.path.exists('logs'):
    os.makedirs('logs')

for level in levels:
    utc_datetime = datetime.datetime.utcnow()
    formated_string = utc_datetime.strftime("%Y-%m-%d-%H%MZ")
    filename = os.path.join('logs', (os.path.basename(level) + '_%s.txt') % formated_string)
    if args.graph:
        cmd = "java -jar server.jar -l %s -g -p -c \"java -classpath bin;lib\* dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format=\"%%1$tY-%%1$tm-%%1$td %%1$tH:%%1$tM:%%1$tS %%4$s %%2$s %%5$s%%6$s%%n\"\" > %s 2>&1" % (level, filename)
    else:
        cmd = "java -jar server.jar -l %s -c \"java -classpath bin;lib\* dk.dtu.ai.blueducks.BlueDucksClient -Djava.util.logging.SimpleFormatter.format=\"%%1$tY-%%1$tm-%%1$td %%1$tH:%%1$tM:%%1$tS %%4$s %%2$s %%5$s%%6$s%%n\"\" > %s 2>&1" % (level, filename)
    print '[%s] executing %s ...' % (utc_datetime, level)
    ret = subprocess.call(cmd, shell = True)