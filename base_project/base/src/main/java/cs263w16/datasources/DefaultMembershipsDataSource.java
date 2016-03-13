package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.AppUser;
import cs263w16.model.Event;
import cs263w16.model.Subscription;
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

    public void addMembership(String userId, String community) {
        usersDataSource.addMembership(userId, community);
    }

    public void removeMembership(String userId, String community) {
        usersDataSource.removeMembership(userId, community);
    }

    public List<String> getMemberships(String userId) {
        AppUser appUser = usersDataSource.getUser(userId);
        if (appUser == null) {
            log.warning("Get Memberships could not access user");
            return null;
        }
        List<String> memberships = appUser.getMemberships();
        return (memberships != null) ? memberships : (new ArrayList<String>());
    }

    public void addSubscription(String userId, String communityId, String eventId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException {
        String key = userId + ":" + communityId + ":" + eventId;
        Entity entity = new Entity("Subscription", key);
        entity.setProperty("membership", userId + ":" + communityId);
        entity.setProperty("event", eventId);

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
}
