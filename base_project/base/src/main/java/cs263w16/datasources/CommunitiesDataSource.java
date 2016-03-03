package cs263w16.datasources;

import cs263w16.model.Community;
import cs263w16.model.Event;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface CommunitiesDataSource {

    /**
     * Retrieve the data entry for the specified community.
     * @param communityId
     * @return
     */
    public Community getCommunity(String communityId);

    /**
     * Add a data entry for a new community.
     * @param community
     */
    public void addCommunity(Community community);

    /**
     * Get all events for the specified community.
     * @param communityId
     * @return
     */
    public List<Event> eventsForCommunity(String communityId);

    /**
     * Get all communities whose names start with pattern.
     * @param pattern
     * @return
     */
    public List<Community> queryCommunitiesPrefix(String pattern);
}
