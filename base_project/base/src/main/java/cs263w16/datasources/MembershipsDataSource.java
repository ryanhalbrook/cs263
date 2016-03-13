package cs263w16.datasources;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.model.Announcement;
import cs263w16.model.Event;
import cs263w16.model.Membership;
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

    /**
     * Get all memberships belonging to the specified user.
     * @param userId
     */
    List<Membership> getMemberships(String userId);

    List<Event> getMembershipEventsForUser(String userId);

    void hideMembershipEvent(String userId, String eventId) throws EntityNotFoundException;

    /**
     * Subscribe the user to the event.
     * @param userId
     * @param communityId
     * @param eventId
     * @throws DatastoreFailureException
     * @throws ConcurrentModificationException
     * @throws IllegalArgumentException
     */
    void addSubscription(String userId, String communityId, String eventId)
            throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    /**
     * Get a list of subscriptions for the specified user.
     * @param userId
     * @param communityId
     * @return
     */
    List<Subscription> getSubscriptions(String userId, String communityId);

    void addAnnouncement(Announcement announcement)
            throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;

    Announcement getAnnouncement(String announcementId) throws EntityNotFoundException;

    void propagateAnnouncement(Announcement announcement);

    /**
     * Create a reference to the event for every member of the community.
     * @param eventId
     * @param communityId
     */
    void propagateEvent(String eventId, String communityId);
}
