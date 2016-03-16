package cs263w16.resources;


import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
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

    private String communityId;

    public CommunityEventsResource(UriInfo uriInfo, Request request, String communityId)
    {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityId = communityId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents()
    {
        // check - community exists.
        Community community = communitiesDataSource.getCommunity(communityId);
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Community not found for community id: " + communityId).build();
        }

        return Response.ok().entity(communitiesDataSource.eventsForCommunity(communityId)).build();
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

        if (name == null || description == null || date == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";
        String descRegExp = "\\A[a-zA-Z0-9 \\.]+\\z";
        String dateexp = "\\A[\\d]{4}-[\\d]{2}-[\\d]{2}T[\\d]{2}:[\\d]{2}:[\\d]{2}\\.[\\d]{3}Z\\z";

        if (!Pattern.matches(regexp, name) || !Pattern.matches(descRegExp, description) || !Pattern.matches(dateexp, date)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // check - community exists.
        Community community = communitiesDataSource.getCommunity(communityId);
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Community not found for community id: " + communityId).build();
        }

        // check - user is the community admin.
        if (!userId.equals(community.getAdminUserId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // TODO: Check if event already exists.

        try {

            if (date == null) {
                System.out.println("Date is null");
            }

            Event event = new Event(name, description, communityId);
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime dt = fmt.parseDateTime(date);
            event.setDate(dt.toDate());
            eventsDataSource.putEvent(event);

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/tasks/propagateeventworker")
                    .param("eventid", event.getId())
                    .param("communityid", communityId));

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("{event}")
    public EventResource getEvent(@PathParam("event") String id) {

        if (id == null) {
            return null;
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, id)) {
            return null;
        }

        return new EventResource(uriInfo, request, id);

    }


}
