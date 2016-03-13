package cs263w16.datasources;

import com.google.appengine.api.datastore.DatastoreFailureException;
import cs263w16.model.Event;
import cs263w16.model.Subscription;

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
    void addMembership(String userId, String communityId);

    /**
     * Remove the community membership from the specified user.
     * @param userId
     * @param community
     */
    void removeMembership(String userId, String community);


    void addSubscription(String userId, String communityId, String eventId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    List<Subscription> getSubscriptions(String userId, String communityId);

    /**
     * Get all memberships from the specified user.
     * @param userId
     */
    List<String> getMemberships(String userId);

    void propagateEvent(String eventId, String communityId);
}
