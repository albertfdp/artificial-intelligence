#!/usr/bin/python
import sys
import os
import re
from optparse import OptionParser

optparser = OptionParser()
optparser.add_option('-f', '--file', dest = 'filename', help = 'FILE to parse')
(options, args) = optparser.parse_args()

# check that all mandatory options are provided
for m in ['filename']:
	if not options.__dict__[m]:
		print 'mandatory option is missing\n'
		optparser.print_help()
		exit(-1)

if not os.path.isfile(options.filename):
	print 'file %s could not be found' % options.filename
	exit(-1)
	


class parser:

	COLORS = ['blue', 'red', 'green', 'cyan', 'magenta', 'orange',
		'pink', 'yellow']
		
	REGEXP_COLOR_DECLARATION = '^(red|blue|green|cyan|magenta|orange|pink|yellow):\s(\d),([A-Z]).*$'

	def __init__(self, filename):
		self.filename = filename
		self.regexp_color = re.compile(self.REGEXP_COLOR_DECLARATION)
		
	def parse(self):
		for rnum, row in enumerate(open(self.filename, 'r').readlines()):
			m = self.regexp_color.match(row)
			if m:
				color, agent, box = m.group(1), m.group(2), m.group(3)
				print color, agent, box
			else:
				for cnum, col in enumerate(row):
					location = '%d-%d' % (rnum, cnum)
					if self.is_box(col):
						print 'box %s: %s' % (col, location)
					if self.is_agent(col):
						print 'agent %s: %s' % (col, location)
					if self.is_goal_cell(col):
						print 'goal_cell %s: %s' % (col, location)
					if self.is_unknown(col):
						print 'unknown: %s' % location
	
	"""
		Wall items are represented by the symbol +
	"""
	def is_wall(self, item):
		return item == '+'

	"""
		Agents are represented by the numbers 0, 1, ..., 9.
	"""
	def is_agent(self, item):
		return self.matches('^[\d]$', item)
	
	"""
		Boxes are represented by capital letters A, B, ..., Z.
	"""
	def is_box(self, item):
		return self.matches('^[A-Z]$', item)
		
	"""
		Goal cells are represented by small letters a, b, ..., z.
	"""
	def is_goal_cell(self, item):
		return self.matches('^[a-z]$', item)
	
	"""
		Unknown items are represented by the symbol *
	"""
	def is_unknown(self, item):
		return self.matches('^[\*]$', item)
		
	def is_location(self, item):
		return (self.is_box(item) or self.is_goal_cell(item) or self.is_agent(item) \
			or self.matches('\s', item))
		
	def matches(self, pattern, item):
		if re.match(pattern, str(item)):
			return True
		return False

p = parser(options.filename)
p.parse()