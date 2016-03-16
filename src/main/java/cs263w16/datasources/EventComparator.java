package cs263w16.datasources;
import cs263w16.model.Event;

import java.util.Comparator;

/**
 * Created by ryanhalbrook on 3/16/16.
 */
public class EventComparator implements Comparator<Event> {
    public int compare(Event a, Event b) {
        return a.getDate().compareTo(b.getDate());
    }
}
