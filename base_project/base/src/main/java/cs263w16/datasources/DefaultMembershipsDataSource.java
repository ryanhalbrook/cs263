package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.*;
import cs263w16.resources.CommunityResource;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultMembershipsDataSource implements MembershipsDataSource {

    private DatastoreService datastore;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private static UsersDataSource usersDataSource = new DefaultUsersDataSource();
    public DefaultMembershipsDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void addMembership(String userId, String communityId) {

        Transaction txn = datastore.beginTransaction();
        try {
            try {

                Entity membership = new Entity("Membership", userId + ":" + communityId);
                membership.setProperty("userid", userId);
                membership.setProperty("communityid", communityId);
                try {
                    datastore.put(membership);
                    txn.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warning("Failed to add membership");
                }

            } catch (Exception e) {
                log.warning("AppUser not found, failed to add membership");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    public void removeMembership(String userId, String communityId) {
        datastore.delete(KeyFactory.createKey("Membership", userId + ":" + communityId));
    }

    public List<Membership> getMemberships(String userId) {

        if (usersDataSource.getUser(userId) == null) {
            return null;
        }

        Query.Filter filter =
                new Query.FilterPredicate("userid",
                        Query.FilterOperator.EQUAL,
                        userId);

        Query q = new Query("Membership").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) {
            return null;
        }

        List<Membership> memberships = new ArrayList<>();

        for (Entity entity : entities) {
            Membership membership = new Membership((String)entity.getProperty("communityid"), (String)entity.getProperty("userid"));
            memberships.add(membership);
        }

        return memberships;

    }

    public void addSubscription(String userId, String communityId, String eventId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException {
        String key = userId + ":" + communityId + ":" + eventId;
        Entity entity = new Entity("Subscription", key);
        entity.setProperty("membership", userId + ":" + communityId);
        entity.setProperty("event", eventId);
        entity.setProperty("userid", userId);

        datastore.put(entity);
    }

    public List<Subscription> getSubscriptions(String userId, String communityId) {

        String membershipId = userId + ":" + communityId;

        Query.Filter filter =
                new Query.FilterPredicate("membership",
                        Query.FilterOperator.EQUAL,
                        membershipId);

        Query q = new Query("Subscription").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) return new ArrayList<>();

        List<Subscription> subscriptions = new ArrayList<>();
        for (Entity entity : entities) {
            Subscription subscription = new Subscription(userId, communityId, (String)entity.getProperty("event"));
            subscriptions.add(subscription);
        }

        return subscriptions;

    }

    public void propagateEvent(String eventId, String communityId) {
        // Get all userIds who are members of the communityId community
        List<String> userIds = new ArrayList<>();
        Query.Filter filter = new Query.FilterPredicate("communityid", Query.FilterOperator.EQUAL, communityId);

        Query q = new Query("Membership").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) return;

        for (Entity entity : entities) {
            userIds.add((String)entity.getProperty("userid"));
        }

        System.out.println("Found " + userIds.size() + " user ids");

        for (String userId : userIds) {
            Entity membershipEvent = new Entity("MembershipEvent", userId + ":" + eventId);
            membershipEvent.setProperty("hidden", Boolean.valueOf(false));
            membershipEvent.setProperty("eventid", eventId);
            membershipEvent.setProperty("userid", userId);
            datastore.put(membershipEvent);
        }

        // TODO: We added them all, now grab the event entity and update the propagated flag.

    }

    public void addAnnouncement(Announcement announcement) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException {
        String key = announcement.getEventId() + ":" + announcement.getTitle();
        Entity entity = new Entity("Announcement", key);
        entity.setProperty("title", announcement.getTitle());
        entity.setProperty("description", announcement.getDescription());
        entity.setProperty("eventid", announcement.getEventId());

        datastore.put(entity);
    }

    public Announcement getAnnouncement(String announcementId) throws EntityNotFoundException {
        Key key = KeyFactory.createKey("Announcement", announcementId);
        Entity entity = datastore.get(key);

        Announcement announcement = new Announcement (
                (String)entity.getProperty("eventid"),
                (String)entity.getProperty("title"),
                (String)entity.getProperty("description"));

        return announcement;
    }

    public void propagateAnnouncement(Announcement announcement) {
        // Get all userIds who are subscribed to the announcement's event.
        List<String> membershipIds = new ArrayList<>();

        Query.Filter filter = new Query.FilterPredicate("event", Query.FilterOperator.EQUAL, announcement.getEventId());

        Query q = new Query("Subscription").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) return;

        for (Entity entity : entities) {
            membershipIds.add((String)entity.getProperty("membership"));
        }

        System.out.println("Found " + membershipIds.size() + " membership ids");

        for (String membershipId : membershipIds) {
            Entity subscriptionAnnouncement = new Entity("SubscriptionAnnouncement",
                    membershipId + ":" + announcement.getEventId() + ":" + announcement.getTitle());
            subscriptionAnnouncement.setProperty("title", announcement.getTitle());
            subscriptionAnnouncement.setProperty("description", announcement.getDescription());
            subscriptionAnnouncement.setProperty("membershipid", membershipId);

            datastore.put(subscriptionAnnouncement);
        }


    }
}
