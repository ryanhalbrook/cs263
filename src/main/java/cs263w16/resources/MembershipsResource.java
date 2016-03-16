package cs263w16.resources;

import cs263w16.datasources.*;
import cs263w16.model.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 2/18/16.
 */
@Path("/memberships")
public class MembershipsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public static MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();
    public static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response newMembership(@PathParam("communityid") String communityId,
                              @Context HttpServletResponse servletResponse,
                              @Context HttpHeaders headers) throws IOException {

        if (communityId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, communityId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String user = headers.getRequestHeader("userid").get(0);
        membershipsDataSource.addMembership(user, communityId);

        return Response.status(Response.Status.CREATED).build();

    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Community> getMemberships(@Context HttpServletResponse servletResponse,
                                          @Context HttpHeaders headers) {
        String userId = headers.getRequestHeader("userid").get(0);
        if (userId == null) return null;

        List<Community> communities = new ArrayList<>();
        List<Membership> memberships = membershipsDataSource.getMemberships(userId);
        if (memberships == null) {
            System.out.println("Memberships is null");
        } else {
            for (Membership membership : membershipsDataSource.getMemberships(userId)) {
                Community c = communitiesDataSource.getCommunity(membership.getCommunityId());
                if (c != null) communities.add(c);
            }
        }

        return communities;

    }

    @Path("{membership}")
    public MembershipResource getMembership(@PathParam("membership") String id) {

        if (id == null) {
            return null;
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, id)) {
            return null;
        }

        return new MembershipResource(uriInfo, request, id);

    }


}
