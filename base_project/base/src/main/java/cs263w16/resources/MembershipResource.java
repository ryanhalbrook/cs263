package cs263w16.resources;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.datasources.DefaultMembershipsDataSource;
import cs263w16.datasources.MembershipsDataSource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class MembershipResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityId;

    public static MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();

    public MembershipResource(UriInfo uriInfo, Request request, String communityId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityId = communityId;
    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public Response deleteMembership(@Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) {
        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        membershipsDataSource.removeMembership(userId, communityId);
        return Response.noContent().build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putMembership(@Context HttpHeaders headers) {

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        membershipsDataSource.addMembership(userId, communityId);
        return Response.status(Response.Status.CREATED).build();


    }


    @Path("events/{event}")
    public MembershipEventResource getMembershipEvent(@PathParam("event") String eventId) {

        return new MembershipEventResource(uriInfo, request, communityId, eventId);

    }

}
