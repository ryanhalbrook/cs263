package cs263w16.resources;

import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.datasources.DefaultMembershipsDataSource;
import cs263w16.datasources.MembershipsDataSource;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 3/14/16.
 */
public class MembershipEventResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityId;
    private String eventId;

    public static MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();

    public MembershipEventResource(UriInfo uriInfo, Request request, String communityId, String eventId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityId = communityId;
        this.eventId = eventId;
    }

    /**
     * Hides the membership event.
     * @param servletResponse
     * @param headers
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMembershipEvent(@Context HttpServletResponse servletResponse,
                                     @Context HttpHeaders headers) {
        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            membershipsDataSource.hideMembershipEvent(userId, eventId);
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
        return Response.noContent().build();
    }



}
