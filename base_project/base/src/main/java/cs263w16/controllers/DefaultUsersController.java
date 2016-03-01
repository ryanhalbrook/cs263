package cs263w16.controllers;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import cs263w16.model.AppUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultUsersController implements UsersController {
    private DatastoreService datastore;

    public DefaultUsersController() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public AppUser getUser(String id) {
        Key key = KeyFactory.createKey("AppUser", id);
        AppUser user;
        try {
            Entity entity = datastore.get(key);
            String emailAddress = (String)entity.getProperty("emailAddress");
            String userName = id;//(String)entity.getProperty("userName");
            String firstName = (String)entity.getProperty("firstName");
            String lastName = (String)entity.getProperty("lastName");
            Date date = (Date)entity.getProperty("date");
            ArrayList<String> memberships = (ArrayList<String>)entity.getProperty("memberships");
            System.out.println("count(memberships): " + memberships.size());

            user = new AppUser(id, emailAddress, userName, firstName, lastName, date);
            user.setMemberships(memberships);


        } catch (EntityNotFoundException e) {
            user = null;
        }

        return user;
    }

    public AppUser getUserByUserName(String username) {
        Filter userNameFilter = new FilterPredicate("userName", FilterOperator.EQUAL, username);
        Query q = new Query("AppUser").setFilter(userNameFilter);
        PreparedQuery pq = datastore.prepare(q);

        List<Entity> entities = pq.asList(FetchOptions.Builder.withDefaults());
        if (entities.size() > 1) {
            throw new RuntimeException("More than one user with the specified user name.");
        } else if (entities.size() < 1) {
            return null;
        }

        Entity entity = entities.get(0);
        AppUser user;

        String emailAddress = (String)entity.getProperty("emailAddress");
        String userName = (String)entity.getProperty("userName");
        String firstName = (String)entity.getProperty("firstName");
        String lastName = (String)entity.getProperty("lastName");
        Date date = (Date)entity.getProperty("date");
        ArrayList<String> memberships = (ArrayList<String>)entity.getProperty("memberships");

        user = new AppUser(entity.getKey().getName(), emailAddress, userName, firstName, lastName, date);
        user.setMemberships(memberships);

        return user;

    }

    public void addUser(AppUser appUser) {
        Entity userEntity = new Entity("AppUser", appUser.getUserId());
        userEntity.setProperty("emailAddress", appUser.getEmailAddress());
        userEntity.setProperty("userName", appUser.getUserName());
        userEntity.setProperty("firstName", appUser.getFirstName());
        userEntity.setProperty("lastName", appUser.getLastName());
        userEntity.setProperty("date", appUser.getSignupDate());

        datastore.put(userEntity);
    }
}
