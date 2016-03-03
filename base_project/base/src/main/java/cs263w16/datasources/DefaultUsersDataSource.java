package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.AppUser;
import cs263w16.resources.CommunityResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultUsersDataSource implements UsersDataSource {
    private DatastoreService datastore;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public DefaultUsersDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public AppUser getUser(String userId) {

        AppUser user;

        try {
            Key key = KeyFactory.createKey("AppUser", userId);

            Entity entity = datastore.get(key);
            String emailAddress = (String)entity.getProperty("emailAddress");
            String userName = (String)entity.getProperty("userName");
            String firstName = (String)entity.getProperty("firstName");
            String lastName = (String)entity.getProperty("lastName");
            Date date = (Date)entity.getProperty("date");
            ArrayList<String> memberships = (ArrayList<String>)entity.getProperty("memberships");

            user = new AppUser(userId, emailAddress, userName, firstName, lastName, date);
            user.setMemberships(memberships);


        } catch (EntityNotFoundException e) {
            user = null;
            log.warning("Get User: user not found");
        }

        return user;
    }

    public void addUser(AppUser appUser) {

        if (getUser(appUser.getUserId()) != null) {
            return;
        }
        Entity userEntity = new Entity("AppUser", appUser.getUserId());
        userEntity.setProperty("emailAddress", appUser.getEmailAddress());
        userEntity.setProperty("userName", appUser.getUserName());
        userEntity.setProperty("firstName", appUser.getFirstName());
        userEntity.setProperty("lastName", appUser.getLastName());
        userEntity.setProperty("date", appUser.getSignupDate());
        userEntity.setProperty("memberships", appUser.getMemberships());

        try {
            datastore.put(userEntity);
        } catch (Exception e) {
            log.warning("Exception thrown attempting to add new user entry");
        }

    }

    public void addMembership(String userId, String community) {

        Transaction txn = datastore.beginTransaction();
        try {
            try {
                Key userKey = KeyFactory.createKey("AppUser", userId);
                Entity entity = datastore.get(userKey);

                List<String> memberships = (List<String>) entity.getProperty("memberships");
                if (memberships == null) {
                    memberships = new ArrayList<String>();
                }
                if (memberships.contains(community)) {
                    log.info("Attempt made to re-add membership");
                } else {
                    memberships.add(community);
                    entity.setProperty("memberships", memberships);
                    try {
                        datastore.put(entity);
                        txn.commit();
                    } catch (Exception e) {
                        log.warning("Failed to update AppUser with new membership");
                    }
                }

            } catch (EntityNotFoundException e) {
                log.warning("AppUser not found, failed to add membership");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    public void removeMembership(String userId, String community) {
        Transaction txn = datastore.beginTransaction();
        try {
            try {
                Key userKey = KeyFactory.createKey("AppUser", userId);
                Entity entity = datastore.get(userKey);

                List<String> memberships = (List<String>) entity.getProperty("memberships");
                if (memberships == null) {
                    memberships = new ArrayList<String>();
                }
                if (memberships.contains(community)) {
                    memberships.remove(community);
                    entity.setProperty("memberships", memberships);
                    try {
                        datastore.put(entity);
                        txn.commit();
                    } catch (Exception e) {
                        log.warning("Failed to update AppUser with removed membership");
                    }
                } else {
                    log.info("Attempt made to remove non-existing membership");
                }

            } catch (EntityNotFoundException e) {
                log.warning("AppUser not found, failed to add membership");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

}
