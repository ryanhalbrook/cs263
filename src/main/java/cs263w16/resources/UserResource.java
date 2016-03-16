package cs263w16.resources;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;

import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.datasources.DefaultUsersDataSource;
import cs263w16.datasources.UsersDataSource;
import cs263w16.model.AppUser;
import cs263w16.StringXMLWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

// References:
// A technique for determining the logged in user from inside of javascript:
// http://stackoverflow.com/questions/13055032/authenticated-app-on-appengine-with-angularjs

import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 2/18/16.
 */
@Path("user")
public class UserResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public static UsersDataSource usersDataSource = new DefaultUsersDataSource();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addUser(
                        @FormParam("inputFirstName") String firstname,
                        @FormParam("inputLastName") String lastname,
                        @FormParam("inputUserName") String username,
                        @Context HttpServletResponse servletResponse) {

        if (firstname == null || lastname == null || username == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";

        if (!Pattern.matches(regexp, firstname) || !Pattern.matches(regexp, lastname) || !Pattern.matches(regexp, username)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {

            log.info("Queuing new user worker task");

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/tasks/newuserworker")
                    .param("inputEmail", user.getEmail())
                    .param("inputFirstName", firstname)
                    .param("inputLastName", lastname)
                    .param("inputUserName", username));

        }

        return Response.status(Response.Status.ACCEPTED).build();

    }

    @Path("loginurl")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StringXMLWrapper getLoginUrl() {

        UserService userService = UserServiceFactory.getUserService();
        return new StringXMLWrapper(userService.createLoginURL("/signup.jsp"));

    }

    @Path("logouturl")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StringXMLWrapper getLogoutUrl() {

        UserService userService = UserServiceFactory.getUserService();
        return new StringXMLWrapper(userService.createLogoutURL("/main.html"));

    }

    @Path("activeuser")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AppUser getUser() {

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user == null) return null;

        AppUser appUser = null;
        try {
            appUser = usersDataSource.getUser(user.getEmail());
        } catch (EntityNotFoundException e) {
            appUser = null;
        }

        return appUser;

    }

}
