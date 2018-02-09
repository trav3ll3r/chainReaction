from abc import ABCMeta, abstractmethod

class Chain():
	"""Chain"""
	__metaclass__ = ABCMeta

	def __init__(self, name):
		self.chains = []
		self.reactions = []
		self.name = name

		defaultSuccess = Reaction()
		self.reactions.append()

	def execute(self):
		self.log("Begin chain with %s chains" % len(self.chains))
		self.__do__()
		self.react(self)
		self.log("End chain")
		return self

	# @abstractmethod
	def __do__(self):
		for chain in self.chains:
			chain_result = chain.execute()
		

	def react(self, chain):
		"""return"""
		chain_type = chain.__class__.__name__
		self.log("Begin reaction for [%s]" % chain_type)

		for reaction in self.reactions:
			# if reaction reaction_type
			pass

		self.log("End reaction for [%s]" % chain_type)

	def add(self, chain):
		self.chains.append(chain)

	def log(self, message):
		log_message(self.name, message)


class Reaction(object):
	def __init__(self, for_class):
		self.for_class = for_class

	def react(self, chain):
		if ()


def log_message(tag, message):
	print("%s: %s" % (tag, message))