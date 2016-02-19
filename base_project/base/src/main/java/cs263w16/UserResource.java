package cs263w16;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.AppDao.AppDaoFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.logging.Logger;

// References:
// A technique for determining the logged in user from inside of javascript:
// http://stackoverflow.com/questions/13055032/authenticated-app-on-appengine-with-angularjs

/**
 * Created by ryanhalbrook on 2/18/16.
 */
@Path("activeuser")
public class UserResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AppUser getUser()
    {

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find out who the user is.

        if (user == null) {
            System.out.println("User is null");
            return null;
        }

        AppUser appUser = null;

        try {
            Entity entity = getDatastoreService().get(KeyFactory.createKey("AppUser", user.getUserId()));

            String id = "";
            String email = (String)entity.getProperty("emailAddress");
            String userName = (String)entity.getProperty("userName");
            String firstName = (String)entity.getProperty("firstName");
            String lastName = (String)entity.getProperty("lastName");
            Date date = (Date)entity.getProperty("date");

            appUser = new AppUser(id, email, userName, firstName, lastName, date);

        } catch (EntityNotFoundException e) {
            throw new RuntimeException("User not found");
        }

        //return (appUser != null ? appUser.getUserName() : "nouser");
        return appUser;
    }

    private DatastoreService getDatastoreService()
    {
        if (_datastore == null) {
            _datastore = DatastoreServiceFactory.getDatastoreService();
            if (_datastore == null) {
                log.info("Failed to acquire Datastore Service object");
            }
        }
        return _datastore;
    }

}
