package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.*;
import cs263w16.resources.CommunityResource;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultMembershipsDataSource implements MembershipsDataSource {

    private DatastoreService datastore;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private UsersDataSource usersDataSource = new DefaultUsersDataSource();
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

        // Add MembershipEvents for all of the community's events.
        Query.Filter filter =
                new Query.FilterPredicate("communityName",
                        Query.FilterOperator.EQUAL,
                        communityId);

        Query q = new Query("Event").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) {
            return;
        }

        for (Entity entity : entities) {

            String eventId = entity.getKey().getName();
            Date date = (Date)entity.getProperty("eventDate");

            Entity membershipEvent = new Entity("MembershipEvent", userId + ":" + eventId);
            membershipEvent.setProperty("hidden", Boolean.valueOf(false));
            membershipEvent.setProperty("eventid", eventId);
            membershipEvent.setProperty("userid", userId);
            membershipEvent.setProperty("eventDate", date);
            membershipEvent.setProperty("communityid", communityId);
            datastore.put(membershipEvent);
        }

    }

    public void removeMembership(String userId, String communityId) {
        datastore.delete(KeyFactory.createKey("Membership", userId + ":" + communityId));

        // Delete all MembershipEvents
        Query.Filter userIdFilter =
                new Query.FilterPredicate("userid",
                        Query.FilterOperator.EQUAL,
                        userId);

        Query.Filter communityIdFilter =
                new Query.FilterPredicate("communityid",
                        Query.FilterOperator.EQUAL,
                        communityId);

        Query.CompositeFilter filter =
                new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(userIdFilter, communityIdFilter));

        Query q = new Query("MembershipEvent").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) {
            return;
        }

        for (Entity entity : entities) {
            datastore.delete(KeyFactory.createKey("MembershipEvent",entity.getKey().getName()));
        }

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

        Date date = null;

        try {
            Entity entity = datastore.get(KeyFactory.createKey("Event", eventId));
            date = (Date)entity.getProperty("eventDate");
        } catch (EntityNotFoundException e) {

        }

        System.out.println("Found " + userIds.size() + " user ids");

        for (String userId : userIds) {
            Entity membershipEvent = new Entity("MembershipEvent", userId + ":" + eventId);
            membershipEvent.setProperty("hidden", Boolean.valueOf(false));
            membershipEvent.setProperty("eventid", eventId);
            membershipEvent.setProperty("userid", userId);
            membershipEvent.setProperty("eventDate", date);
            membershipEvent.setProperty("communityid", communityId);
            datastore.put(membershipEvent);
        }

        // TODO: We added them all, now grab the event entity and update the propagated flag.

    }

    public List<Event> getMembershipEventsForUser(String userId) {

        Query.Filter userIdfilter =
                new Query.FilterPredicate("userid", Query.FilterOperator.EQUAL, userId);

        Date now = new Date();

        Query.Filter upcomingFilter =
                new Query.FilterPredicate("eventDate", Query.FilterOperator.GREATER_THAN, now);

        Query.CompositeFilter filter =
                new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(userIdfilter, upcomingFilter));

        Query q = new Query("MembershipEvent").setFilter(filter).addSort("eventDate", Query.SortDirection.ASCENDING);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        List<String> eventIds = new ArrayList<>();

        for (Entity entity : entities) {
            Boolean hidden = (Boolean)entity.getProperty("hidden");
            if (hidden.equals(Boolean.valueOf(false))) {
                eventIds.add((String) entity.getProperty("eventid"));
            }
        }

        List<Event> events = new ArrayList<>();

        for (String eventId : eventIds) {
            Entity entity;
            try {
                entity = datastore.get(KeyFactory.createKey("Event", eventId));
                Event event = new Event(  eventId,
                        (String)entity.getProperty("description"),
                        (String)entity.getProperty("communityName"),
                        (Boolean)entity.getProperty("publiclyAvailable"));

                event.setEventDate((Date)entity.getProperty("eventDate"));

                events.add(event);
            } catch (EntityNotFoundException e) {
                // Oh well, live and log
                log.info("Thought there was a MembershipEvent, but it dissappeared...");
            }
        }

        return events;

    }

    public void hideMembershipEvent(String userId, String eventId) throws EntityNotFoundException {
        Entity entity;
        entity = datastore.get(KeyFactory.createKey("MembershipEvent", userId + ":" + eventId));
        entity.setProperty("hidden", Boolean.valueOf(true));
        datastore.put(entity);
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
            subscriptionAnnouncement.setProperty("eventid", announcement.getEventId());

            datastore.put(subscriptionAnnouncement);
        }
    }

    public List<Announcement> getSubscriptionAnnouncementsForUser(String userId) {
        List<Announcement> announcements = new ArrayList<>();

        Query.Filter filter = new Query.FilterPredicate("userid", Query.FilterOperator.EQUAL, userId);

        Query q = new Query("Membership").setFilter(filter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            String membershipId = entity.getKey().getName();

            Query.Filter membershipFilter = new Query.FilterPredicate("membershipid", Query.FilterOperator.EQUAL, membershipId);

            Query announcementQuery = new Query("SubscriptionAnnouncement").setFilter(membershipFilter);

            List<Entity> announcementEntities = datastore.prepare(announcementQuery).asList(FetchOptions.Builder.withDefaults());

            for (Entity announcementEntity : announcementEntities) {
                announcements.add(new Announcement((String)announcementEntity.getProperty("eventid"),
                        (String)announcementEntity.getProperty("title"),
                        (String)announcementEntity.getProperty("description")));
            }


        }

        return announcements;

    }

}
