package cs263w16.datasources;

import com.google.appengine.api.datastore.DatastoreFailureException;
import cs263w16.model.Event;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface MembershipsDataSource {

    /**
     * Add a community membership to the specified user.
     * @param userId
     * @param communityId
     */
    public void addMembership(String userId, String communityId);

    /**
     * Remove the community membership from the specified user.
     * @param userId
     * @param community
     */
    public void removeMembership(String userId, String community);


    public void addSubscription(String userId, String communityId, String eventId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    public List<Event> getSubscriptions(String userId, String communityId);

    /**
     * Get all memberships from the specified user.
     * @param userId
     */
    public List<String> getMemberships(String userId);
}
