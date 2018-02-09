from model import *
import time

delay_factor = 1

class L1_Link(Chain):
	# def do(self):
		pass

class L1_1_Link(Chain):
	# def do(self):
		pass

class L1_1_1_Link(Chain):
	def __do__(self):
		worktime = 0.5 * delay_factor
		self.log("doing work for %s seconds" % worktime)
		time.sleep(worktime)
		self.log("finished work")
		return self


class L1_1_2_Link(Chain):
	def __do__(self):
		worktime = 0.7 * delay_factor
		self.log("doing work for %s seconds" % worktime)
		time.sleep(worktime)
		self.log("finished work")
		return self


class L1_2_Chain(Chain):
	# def do(self):
		pass

class L1_2_1_Chain(Chain):
	def __do__(self):
		worktime = 0.3 * delay_factor
		self.log("doing work for %s seconds" % worktime)
		time.sleep(worktime)
		self.log("finished work")
		return self


class App(object):
	"""App"""
	def start(self):
		cr = L1_Link("1")

		c1 = L1_1_Link("1-1")
		c1.add(L1_1_1_Link("1-1-1"))
		c1.add(L1_1_2_Link("1-1-2"))
		cr.add(c1)

		c2 = L1_2_Chain("1-2")
		c2.add(L1_2_1_Chain("1-2-1"))
		cr.add(c2)
		
		cr.execute()


if __name__ == '__main__':
	App().start()