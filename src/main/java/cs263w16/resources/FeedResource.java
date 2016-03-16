package cs263w16.resources;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.datasources.*;
import cs263w16.model.AppUser;
import cs263w16.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
@Path("/feed")
public class FeedResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    public MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();


    @DELETE
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@FormParam("eventId") String eventId, @Context HttpHeaders headers) {

        if (eventId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, eventId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        if (headers.getRequestHeader("userid") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String userId = headers.getRequestHeader("userid").get(0);
        if (userId == null || userId.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            membershipsDataSource.hideMembershipEvent(userId, eventId);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.ok().build();

    }

    @Path("events")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents(@Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) {

        if (headers.getRequestHeader("userid") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String userId = headers.getRequestHeader("userid").get(0);
        if (userId == null || userId.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok().entity(membershipsDataSource.getMembershipEventsForUser(userId)).build();
    }

    @Path("announcements")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnnouncements(@Context HttpServletResponse servletResponse,
                              @Context HttpHeaders headers) {

        if (headers.getRequestHeader("userid") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String userId = headers.getRequestHeader("userid").get(0);
        if (userId == null || userId.equals("")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok().entity(membershipsDataSource.getSubscriptionAnnouncementsForUser(userId)).build();
    }


}
