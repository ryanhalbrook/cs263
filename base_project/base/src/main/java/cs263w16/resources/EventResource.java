package cs263w16.resources;

import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.datasources.DefaultEventsDataSource;
import cs263w16.datasources.EventsDataSource;
import cs263w16.model.Community;
import cs263w16.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by ryanhalbrook on 3/11/16.
 */
public class EventResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private EventsDataSource eventsDataSource = new DefaultEventsDataSource();
    private CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();
    private String eventId;

    public EventResource(UriInfo uriInfo, Request request, String eventId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.eventId = eventId;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getEvent() {

        try {
            Event event = eventsDataSource.getEvent(this.eventId);
            return Response.ok().entity(event).build();

        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();

        }

    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteEvent(@Context HttpHeaders headers) {
        String userId;
        Event event;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            event = eventsDataSource.getEvent(this.eventId);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // check - user is the community admin.
        Community community = communitiesDataSource.getCommunity(event.getCommunityName());
        if (community == null) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        if (!userId.equals(community.getAdminUserId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            eventsDataSource.deleteEvent(eventId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
