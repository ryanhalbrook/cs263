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
    Community getCommunity(String communityId);

    /**
     * Add a data entry for a new community.
     * @param community
     */
    void addCommunity(Community community);

    /**
     * Update the data entry for a community.
     * @param community
     */
    void updateCommunity(Community community);

    /**
     * Delete a community.
     * @param communityId
     */
    void deleteCommunity(String communityId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    /**
     * Get all events for the specified community.
     * @param communityId
     * @return
     */
    List<Event> eventsForCommunity(String communityId);

    /**
     * Get all communities whose names start with pattern.
     * @param pattern
     * @return
     */
    List<Community> queryCommunitiesPrefix(String pattern);
}
