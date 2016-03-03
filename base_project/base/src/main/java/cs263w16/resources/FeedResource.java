package cs263w16.resources;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.datasources.DefaultUsersDataSource;
import cs263w16.datasources.UsersDataSource;
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

    public static UsersDataSource usersDataSource = new DefaultUsersDataSource();
    public static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();


    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Event> getEvents(@Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) {
        String userId = headers.getRequestHeader("username").get(0);

        List<Event> events = new ArrayList<>();

        for (String community : usersDataSource.getUser(userId).getMemberships()) {
            events.addAll(communitiesDataSource.eventsForCommunity(community));
        }

        return events;
    }
}
