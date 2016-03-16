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
        Entity entity = ModelTranslator.boxEvent(event);
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
        event = ModelTranslator.unboxEvent(entity);


        return event;
    }


}
