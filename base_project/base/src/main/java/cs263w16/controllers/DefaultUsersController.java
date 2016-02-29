package cs263w16.controllers;

import com.google.appengine.api.datastore.*;
import cs263w16.model.AppUser;

import java.util.Date;

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
            String userName = (String)entity.getProperty("userName");
            String firstName = (String)entity.getProperty("firstName");
            String lastName = (String)entity.getProperty("lastName");
            Date date = (Date)entity.getProperty("date");

            user = new AppUser(id, emailAddress, userName, firstName, lastName, date);
        } catch (EntityNotFoundException e) {
            user = null;
        }

        return user;
    }
}
