package cs263w16.AppDao;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import cs263w16.Community;
import cs263w16.Event;

import java.util.*;


/**
 * Created by ryanhalbrook on 2/11/16.
 */
public class Dao implements AppDao {

    private DatastoreService datastore;

    public Dao(DatastoreService datastore) {
        this.datastore = datastore;
    }

    public List<Event> eventsForCommunity(String communityName)
    {
        Filter keyFilter =
                new FilterPredicate("communityName",
                                    FilterOperator.EQUAL,
                                    communityName);
        Query q = new Query("Event").setFilter(keyFilter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        List<Event> events = new ArrayList<>();
        for (Entity entity : entities) {
            Event event =
                    new Event(entity.getKey().getName(),
                              (String)entity.getProperty("description"),
                              (String)entity.getProperty("communityName"),
                              (Boolean)entity.getProperty("publiclyAvailable"));
            events.add(event);
        }

        return events;
    }

    public void putEvent(Event event)
    {
        Entity entity = new Entity("Event", event.getName());
        entity.setProperty("description", event.getDescription());
        entity.setProperty("communityName", event.getCommunityName());
        entity.setProperty("publiclyAvailable", event.isPubliclyAvailable());

        datastore.put(entity);
    }

    public void putCommunity(Community community)
    {
        Entity entity = new Entity("Community", community.getId());
        entity.setProperty("description", community.getDescription());
        entity.setProperty("creationDate", community.getCreationDate());

        datastore.put(entity);
    }

    /* Fails if there are events that point to this community */
    public void deleteCommunity(String communityName) {
        // TODO stub
    }

    public void deleteEvent(String eventName) {
        // TODO stub
    }

    public Community getCommunity(String communityName) {
        Community community = null;
        try {
            Entity entity = datastore.get(KeyFactory.createKey("Community", communityName));
            community = new Community(communityName, (String)entity.getProperty("description"), (Date)entity.getProperty("creationDate"));
        } catch (EntityNotFoundException e) {

        }
        return community;
    }

    public Event getEvent(String eventName) {
        // TODO stub
        return null;
    }

    public void addMembership(String user, String community) {
        // TODO add checks.

        System.out.println("Username: " + user);

        Filter keyFilter =
                new FilterPredicate("userName",
                        FilterOperator.EQUAL,
                        user);
        Query q = new Query("AppUser").setFilter(keyFilter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if (entities.size() != 1) {
            throw new RuntimeException("User not found");
        }

        Entity entity = entities.get(0);
        List<String> memberships = (List<String>)entity.getProperty("memberships");
        if (memberships == null) memberships = new ArrayList<>();
        if (community == null) throw new RuntimeException("Community must be specified");
        if (!memberships.contains(community)) memberships.add(community);
        entity.setProperty("memberships", memberships);

        datastore.put(entity);

    }

    public List<Community> queryCommunitiesPrefix(String pattern) {
        List <Community> matchingCommunities = new ArrayList<>();

        System.out.println("Pattern = " + pattern);

        Filter gte =
                new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                        FilterOperator.GREATER_THAN_OR_EQUAL,
                        KeyFactory.createKey("Community", pattern));

        Filter lt =
                new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                        FilterOperator.LESS_THAN,
                        KeyFactory.createKey("Community", pattern + "\uFFFD"));

        CompositeFilter filter =
                new CompositeFilter(CompositeFilterOperator.AND, Arrays.asList(gte, lt));

        Query q = new Query("Community").setFilter(filter);
        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        for (Entity e : entities) {
            Community c = new Community(
                    e.getKey().getName(),
                    (String)e.getProperty("description"),
                    (Date)e.getProperty("creationDate"));

            matchingCommunities.add(c);
        }

        return matchingCommunities;
    }

}
