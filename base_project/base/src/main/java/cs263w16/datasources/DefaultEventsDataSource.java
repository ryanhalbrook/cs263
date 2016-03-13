package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.Event;

import java.util.ConcurrentModificationException;
import java.util.Date;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public class DefaultEventsDataSource implements EventsDataSource {

    private DatastoreService datastore;

    public DefaultEventsDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void putEvent(Event event) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException {
        Entity entity = new Entity("Event", event.getName() + ":" + event.getCommunityName());
        entity.setProperty("description", event.getDescription());
        entity.setProperty("communityName", event.getCommunityName());
        entity.setProperty("publiclyAvailable", event.isPubliclyAvailable());
        entity.setProperty("eventDate", event.getEventDate());

        datastore.put(entity);
    }

    public void deleteEvent(String eventId) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException{
        if (eventId == null || eventId.equals("")) {
            return;
        }
        Key key = KeyFactory.createKey("Event", eventId);
        datastore.delete(key);

    }



    public Event getEvent(String eventId) throws EntityNotFoundException {
        if (eventId == null || eventId.equals("")) {
            return null;
        }
        Event event;
        Entity entity = datastore.get(KeyFactory.createKey("Event", eventId));
        event = new Event(  eventId,
                (String)entity.getProperty("description"),
                (String)entity.getProperty("communityName"),
                (Boolean)entity.getProperty("publiclyAvailable"));

        event.setEventDate((Date)entity.getProperty("eventDate"));


        return event;
    }


}
