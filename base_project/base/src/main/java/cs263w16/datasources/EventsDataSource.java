package cs263w16.datasources;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.ExtendableEntityUtil;
import cs263w16.model.Event;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface EventsDataSource {
    public void putEvent(Event event);
    public void deleteEvent(String eventName) throws DatastoreFailureException, ConcurrentModificationException, IllegalArgumentException;
    public Event getEvent(String eventName) throws EntityNotFoundException;
}
