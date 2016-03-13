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
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteEvent(@FormParam("eventId") String eventId, @Context HttpHeaders headers) {
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

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
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
}
