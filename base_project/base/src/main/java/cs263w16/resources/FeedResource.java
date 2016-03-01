package cs263w16.resources;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import cs263w16.controllers.CommunitiesController;
import cs263w16.controllers.DefaultCommunitiesController;
import cs263w16.controllers.DefaultUsersController;
import cs263w16.controllers.UsersController;
import cs263w16.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
@Path("/feed")
public class FeedResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    public static UsersController usersController = new DefaultUsersController();
    public static CommunitiesController communitiesController = new DefaultCommunitiesController();


    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Event> getEvents(@Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) {
        String userId = headers.getRequestHeader("username").get(0);

        List<Event> events = new ArrayList<>();

        for (String community : usersController.getUser(userId).getMemberships()) {
            events.addAll(communitiesController.eventsForCommunity(community));
        }

        return events;
    }
}
