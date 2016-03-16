package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.Community;
import cs263w16.model.Event;
import cs263w16.resources.CommunityResource;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public class DefaultCommunitiesDataSource implements CommunitiesDataSource {

    private DatastoreService datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public DefaultCommunitiesDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void addCommunity(Community community) {

        if (community == null || community.getId() == null || community.getId().equals("")) {
            System.out.println("Problem adding community");
            return;
        }

        if (getCommunity(community.getId()) != null) {
            log.warning("Attempt to add a community that already exists in the data source");
            return;
        }

        Entity entity = ModelTranslator.boxCommunity(community);

        try {
            datastore.put(entity);
        } catch (Exception e) {
            log.warning("Exception thrown attempting to add a new community");
        }

    }

    public void updateCommunity(Community community) {
        if (community == null || community.getId() == null || community.getId().equals("")) {
            return;
        }

        Entity entity = ModelTranslator.boxCommunity(community);

        try {
            datastore.put(entity);
        } catch (Exception e) {
            log.warning("Exception thrown attempting to add a new community");
        }

    }

    public Community getCommunity(String communityName) {
        if (communityName == null || communityName.equals("")) {
            return null;
        }
        Community community;
        try {
            Entity entity = datastore.get(KeyFactory.createKey("Community", communityName));
            community = ModelTranslator.unboxCommunity(entity);

        } catch (EntityNotFoundException e) {
            community = null;
            log.info("Could not find a community that was requested");
        }
        return community;
    }

    public List<Community> queryCommunitiesPrefix(String pattern) {

        if (pattern == null) {
            return null;
        }
        List <Community> matchingCommunities = new ArrayList<>();

        // Prefix Search Technique Information:
        // http://stackoverflow.com/questions/47786/google-app-engine-is-it-possible-to-do-a-gql-like-query
        // http://stackoverflow.com/questions/2900343/google-app-engine-query-data-store-by-a-string-start-with
        // http://stackoverflow.com/questions/1554600/implementing-starts-with-and-ends-with-queries-with-google-app-engine

        Query.Filter gte =
                new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                        Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                        KeyFactory.createKey("Community", pattern));

        Query.Filter lt =
                new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                        Query.FilterOperator.LESS_THAN,
                        KeyFactory.createKey("Community", pattern + "\uFFFD"));

        Query.CompositeFilter filter =
                new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(gte, lt));

        Query q = new Query("Community").setFilter(filter);
        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            Community c = ModelTranslator.unboxCommunity(entity);
            matchingCommunities.add(c);
        }

        return matchingCommunities;
    }

    public List<Event> eventsForCommunity(String communityName) {

        Query.Filter keyFilter =
                new Query.FilterPredicate("community_name",
                        Query.FilterOperator.EQUAL,
                        communityName);
        Query q = new Query("Event").setFilter(keyFilter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        if (entities == null) return new ArrayList<>();

        List<Event> events = new ArrayList<>();
        for (Entity entity : entities) {
            Event event = ModelTranslator.unboxEvent(entity);
            events.add(event);
        }

        return events;
    }

    public void deleteCommunity(String communityId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException{
        if (communityId == null || communityId.equals("")) {
            return;
        }
        Key key = KeyFactory.createKey("Community", communityId);
        datastore.delete(key);

    }

}
