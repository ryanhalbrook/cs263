package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.AppUser;
import cs263w16.resources.CommunityResource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultMembershipsDataSource implements MembershipsDataSource {

    private DatastoreService datastore;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private static UsersDataSource usersDataSource = new DefaultUsersDataSource();
    public DefaultMembershipsDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void addMembership(String userId, String community) {
        usersDataSource.addMembership(userId, community);
    }

    public void removeMembership(String userId, String community) {
        usersDataSource.removeMembership(userId, community);
    }

    public List<String> getMemberships(String userId) {
        AppUser appUser = usersDataSource.getUser(userId);
        if (appUser == null) {
            log.warning("Get Memberships could not access user");
            return null;
        }
        List<String> memberships = appUser.getMemberships();
        return (memberships != null) ? memberships : (new ArrayList<String>());
    }
}
