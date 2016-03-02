package cs263w16.controllers;

import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public interface MembershipsController {
    public void addMembership(String user, String community);
    public void removeMembership(String userId, String community);
    public List<String> getMemberships(String userId);
}
