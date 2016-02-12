package cs263w16;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/11/16.
 */
public class DataModel {

    public static List<Event> eventsForCommunity(DatastoreService datastore, String communityName)
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

    public static void putEvent(DatastoreService datastore, Event event)
    {
        Entity entity = new Entity("Event", event.getName());
        entity.setProperty("description", event.getDescription());
        entity.setProperty("communityName", event.getCommunityName());
        entity.setProperty("publiclyAvailable", event.isPubliclyAvailable());

        datastore.put(entity);
    }

    public static void putCommunity(DatastoreService datastore, Community community)
    {
        Entity entity = new Entity("Community", community.getId());
        entity.setProperty("description", community.getDescription());
        entity.setProperty("creationDate", community.getCreationDate());

        datastore.put(entity);
    }
}
