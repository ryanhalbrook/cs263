package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.Event;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public class DefaultEventsDataSource implements EventsDataSource {

    private DatastoreService datastore;

    public DefaultEventsDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void putEvent(Event event)
    {
        Entity entity = new Entity("Event", event.getName());
        entity.setProperty("description", event.getDescription());
        entity.setProperty("communityName", event.getCommunityName());
        entity.setProperty("publiclyAvailable", event.isPubliclyAvailable());
        entity.setProperty("eventDate", event.getEventDate());

        datastore.put(entity);
    }



    public void deleteEvent(String eventName) {
        // TODO stub
    }



    public Event getEvent(String eventName) {
        // TODO stub
        return null;
    }


}
