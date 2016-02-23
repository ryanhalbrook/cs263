package cs263w16;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.AppDao.AppDaoFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

// References:
// A technique for determining the logged in user from inside of javascript:
// http://stackoverflow.com/questions/13055032/authenticated-app-on-appengine-with-angularjs

/**
 * Created by ryanhalbrook on 2/18/16.
 */
@Path("user")
public class UserResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    @Path("loginurl")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StringXMLWrapper getLoginUrl() {

        UserService userService = UserServiceFactory.getUserService();
        return new StringXMLWrapper(userService.createLoginURL("/signup.jsp"));

    }

    @Path("logouturl")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public StringXMLWrapper getLogoutUrl() {

        UserService userService = UserServiceFactory.getUserService();
        return new StringXMLWrapper(userService.createLogoutURL("/main.html"));

    }

    @Path("activeuser")
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AppUser getUser() {

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        return (user != null) ? AppDaoFactory.getAppDao().getUser(user.getUserId()) : null;

    }

}
