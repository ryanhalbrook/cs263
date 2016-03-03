package cs263w16.datasources;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface MembershipsDataSource {

    /**
     * Add a community membership to the specified user.
     * @param userId
     * @param community
     */
    public void addMembership(String userId, String community);

    /**
     * Add a community membership from the specified user.
     * @param userId
     * @param community
     */
    public void removeMembership(String userId, String community);

    /**
     * Get all memberships from the specified user.
     * @param userId
     */
    public List<String> getMemberships(String userId);
}
