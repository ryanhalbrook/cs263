package cs263w16.controllers;

import cs263w16.model.Community;
import cs263w16.model.Event;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface CommunitiesController {
    public void putCommunity(Community community);

    /* Fails if there are still events for this community */
    public void deleteCommunity(String communityName);

    public Community getCommunity(String communityName);
    public List<Community> queryCommunitiesPrefix(String pattern);
    public List<Event> eventsForCommunity(String communityName);
}
