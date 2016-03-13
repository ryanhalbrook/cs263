package cs263w16.resources;


import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.datasources.DefaultEventsDataSource;
import cs263w16.datasources.EventsDataSource;
import cs263w16.model.Community;
import cs263w16.model.Event;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by ryanhalbrook on 2/12/16.
 */
@Path("/communityevents")
public class CommunityEventsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();
    private static EventsDataSource eventsDataSource = new DefaultEventsDataSource();

    private String communityName;

    public CommunityEventsResource(UriInfo uriInfo, Request request, String communityName)
    {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityName = communityName;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getEvents()
    {
        // check - community exists.
        Community community = communitiesDataSource.getCommunity(communityName);
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Community not found for community id: " + communityName).build();
        }

        return Response.ok().entity(communitiesDataSource.eventsForCommunity(communityName)).build();
    }

    @POST
    //@Produces(MediaType.TEXT_HTML)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newEvent(@FormParam("name") String name,
                             @FormParam("description") String description,
                             @FormParam("date") String date,
                             @Context HttpHeaders headers) throws IOException
    {
        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // check - community exists.
        Community community = communitiesDataSource.getCommunity(communityName);
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Community not found for community id: " + communityName).build();
        }

        // check - user is the community admin.
        if (!userId.equals(community.getAdminUserId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // TODO: Check if event already exists.

        try {
            if (date == null) {
                System.out.println("Seriously");
            }
            Event event = new Event(name, description, communityName, false);
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime dt = fmt.parseDateTime(date);
            event.setEventDate(dt.toDate());
            eventsDataSource.putEvent(event);

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/tasks/propagateeventworker")
                    .param("eventid", name + ":" + communityName)
                    .param("communityid", communityName));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("{event}")
    public EventResource getEvent(@PathParam("event") String id) {

        return new EventResource(uriInfo, request, id);

    }


}
