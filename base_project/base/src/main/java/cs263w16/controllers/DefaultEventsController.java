package cs263w16.controllers;

import com.google.appengine.api.datastore.*;
import cs263w16.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public class DefaultEventsController implements EventsController {

    private DatastoreService datastore;

    public DefaultEventsController() {
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
