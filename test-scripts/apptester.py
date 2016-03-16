# apptester.py

# integration testing for the events app services. 
# Ryan Halbrook, 3/16/16

import sys
from appclient import CommunityClient
from appclient import EventClient


# Happy Case of adding a community, then getting the community. Deletes the community so that the
# test can be run again. 
def test_post_community(community_client):
	name = 'TestCommunity6'
	description = 'This is a test community.'


	# Delete the community.
	response = community_client.delete_community(name)
	if response.status_code != 204 and response.status_code != 417:
		print "test_post_community failed to DELETE, expected status code 204 or 417, got " + str(response.status_code)
		return
	
	# Add a new community. 
	response = community_client.post_community(name, description)
	if response.status_code != 201:
		print "test_post_community failed to POST, expected status code 201, got " + str(response.status_code)
		return

	# Check that the community exists. 
	response = community_client.get_community(name)
	if response.status_code != 200:
		print "test_post_community failed to GET, expected status code 200, got " + str(response.status_code)
		return




# Happy Case of adding a community, then an event, then getting the event. Deletes the community so that the
# test can be run again. 
def test_event(event_client, community_client):

	community_name = 'TestCommunity6'
	community_desc = 'This is a test community.'

	event_name = 'TestEvent2'
	event_desc = 'This is a test event.'

	# Delete the community.
	response = community_client.delete_community(community_name)
	if response.status_code != 204 and response.status_code != 417:
		print "test_post_community failed to DELETE, expected status code 204 or 417, got " + str(response.status_code)
		return

	# Add a new community. 
	response = community_client.post_community(community_name, community_desc)
	if response.status_code != 201:
		print "test_event failed to POST community, expected status code 201, got " + str(response.status_code)
		return

	# Add an event.
	response = event_client.post_event(community_name, event_name, event_desc)
	if response.status_code != 201:
		print "test_event failed to POST event, expected status code 201, got " + str(response.status_code)
		return

	# Get the event.
	response = event_client.get_event(community_name, event_name)
	if response.status_code != 200:
		print "test_post_community failed to GET event, expected status code 200, got " + str(response.status_code)
		return



# Test that parameter validation catches invalid name parameter when creating a community.
def test_param_validation_community_name(community_client, name):

	community_name = name		# this should cause failure
	community_desc = 'test' 	# nothing wrong

	# Add a new community. 
	response = community_client.post_community(community_name, community_desc)
	if response.status_code != 400:
		print "test_param_validation_community_name (" + name + ") failed, expected status code 400, got " + str(response.status_code)
		return


# Test that parameter validation catches invalid name parameter when creating an event.
def test_param_validation_event_name(event_client, name):

	event_name = name		# this should cause failure 
	event_desc = 'test' 	# nothing wrong

	# Add a new community. 
	response = event_client.post_event('testcommunity', event_name, event_desc)
	if response.status_code != 400:
		print "test_param_validation_2 failed, expected status code 400, got " + str(response.status_code)
		return



if __name__ == "__main__":

	if len(sys.argv) < 3:
		exit('Usage: ' + sys.argv[0] + ' <host> <userid>')

	community_client = CommunityClient(sys.argv[1], sys.argv[2])
	event_client = EventClient(sys.argv[1], sys.argv[2])

	test_post_community(community_client)
	test_event(event_client, community_client)

	invalidNames = ['Colon:colon', 'at@', '.', '_', 'test_test']
	for name in invalidNames:
		test_param_validation_community_name(community_client, name)
		test_param_validation_event_name(event_client, name)


