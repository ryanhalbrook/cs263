# appclient.py
# client code to hit the events app services. 
# Ryan Halbrook, 3/16/16

import requests

class CommunityClient:

	#
	# host - protocol, host name, and port - i.e. http://localhost:8080
	#
	def __init__(self, host, userid):
		self.host = host
		self.userid = userid



	def post_community(self, name, description):

		data = {"name":name, "description":description}
		headers = {'userid':self.userid}
		response = requests.post(self.host + '/rest/communities', data = data, headers = headers)
		return response


	def get_community(self, community_id):
		return requests.get(self.host + '/rest/communities/' + community_id)


	def delete_community(self, community_id):

		data = {"communityid": community_id}
		headers = {'userid':self.userid}
		response = requests.delete(self.host + '/rest/communities/' + community_id, data = data, headers = headers)
		return response


	def get_events(self, community_id):
		return requests.get(self.host + '/rest/communities/' + community_id + '/events')



class EventClient:

	#
	# host - protocol, host name, and port - i.e. http://localhost:8080
	#
	def __init__(self, host, userid):
		self.host = host
		self.userid = userid



	def get_event(self, community_id, event_id):
		return requests.get(self.host + '/rest/communities/' + community_id + '/events/' + event_id);


	def post_event(self, community_id, name, description):
		data = {'name': name, 'description': description}
		headers = {'userid':self.userid}
		response = requests.post(self.host + '/rest/communities/events', data = data, headers = headers)
		return response


	def delete_event(self, community_id, event_id):
		headers = {'userid':self.userid}
		response = requests.delete(self.host + '/rest/communities/' + community_id + '/events/' + event_id, headers = headers)
		return response



