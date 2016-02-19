package cs263w16.AppDao;

import cs263w16.Community;
import cs263w16.Event;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/13/16.
 */
public interface AppDao {

    public void putCommunity(Community community);

    /* Fails if there are still events for this community */
    public void deleteCommunity(String communityName);

    public Community getCommunity(String communityName);

    public void putEvent(Event event);
    public void deleteEvent(String eventName);
    public Event getEvent(String eventName);

    public List<Event> eventsForCommunity(String communityName);
    public void addMembership(String user, String community);
    public List<Community> queryCommunitiesPrefix(String pattern);
}
