package cs263w16.resources;

import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.datasources.DefaultEventsDataSource;
import cs263w16.datasources.DefaultMembershipsDataSource;
import cs263w16.datasources.EventsDataSource;
import cs263w16.datasources.MembershipsDataSource;
import cs263w16.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
@Path("/subscriptions")
public class SubscriptionsResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    private MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();
    private EventsDataSource eventsDataSource = new DefaultEventsDataSource();

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscription(@FormParam("eventid") String eventId, @Context HttpHeaders headers) {

        if (eventId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, eventId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (eventId == null || eventId.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Event event;
        try {
            event = eventsDataSource.getEvent(eventId);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        if (event == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        membershipsDataSource.addSubscription(userId, event.getCommunityName(), eventId);

        return Response.status(Response.Status.CREATED).build();
    }

    @Path("{subscription}")
    public SubscriptionResource getSubscription(@PathParam("subscription") String id) {

        if (id == null) {
            return null;
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, id)) {
            return null;
        }

        return new SubscriptionResource(uriInfo, request, id);

    }
}
