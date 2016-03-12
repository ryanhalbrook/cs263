package cs263w16.datasources;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.model.Community;
import cs263w16.model.Event;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface CommunitiesDataSource {

    /**
     * Retrieve the data entry for the specified community.
     * @param communityId
     * @return
     */
    public Community getCommunity(String communityId);

    /**
     * Add a data entry for a new community.
     * @param community
     */
    public void addCommunity(Community community);

    /**
     * Update the data entry for a community.
     * @param community
     */
    public void updateCommunity(Community community);

    /**
     * Delete a community.
     * @param communityId
     */
    public void deleteCommunity(String communityId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    /**
     * Get all events for the specified community.
     * @param communityId
     * @return
     */
    public List<Event> eventsForCommunity(String communityId);

    /**
     * Get all communities whose names start with pattern.
     * @param pattern
     * @return
     */
    public List<Community> queryCommunitiesPrefix(String pattern);
}
