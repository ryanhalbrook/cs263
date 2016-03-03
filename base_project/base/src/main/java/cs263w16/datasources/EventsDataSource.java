package cs263w16.datasources;

import cs263w16.model.Event;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface EventsDataSource {
    public void putEvent(Event event);
    public void deleteEvent(String eventName);
    public Event getEvent(String eventName);
}
