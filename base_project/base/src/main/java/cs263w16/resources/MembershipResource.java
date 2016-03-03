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

/**
 * Created by ryanhalbrook on 2/18/16.
 */
@Path("/memberships")
public class MembershipResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String community;

    public static MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();
    public static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    public MembershipResource(UriInfo uriInfo, Request request, String community) {

        this.uriInfo = uriInfo;
        this.request = request;
        this.community = community;

    }

    public MembershipResource() {}

    @POST
    @Produces(MediaType.TEXT_HTML)
    public void newMembership(@Context HttpServletResponse servletResponse,
                              @Context HttpHeaders headers) throws IOException {

        String user = headers.getRequestHeader("username").get(0);
        membershipsDataSource.addMembership(user, community);

    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    public void deleteMembership(@Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) {
        String user = headers.getRequestHeader("username").get(0);
        membershipsDataSource.removeMembership(user, community);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Community> getMemberships(@Context HttpServletResponse servletResponse,
                                          @Context HttpHeaders headers) {
        String userId = headers.getRequestHeader("username").get(0);
        if (userId == null) return null;

        List<Community> communities = new ArrayList<>();
        List<String> memberships = membershipsDataSource.getMemberships(userId);
        if (memberships == null) {
            System.out.println("Memberships is null");
        } else {
            for (String membership : membershipsDataSource.getMemberships(userId)) {
                Community c = communitiesDataSource.getCommunity(membership);
                if (c != null) communities.add(c);
            }
        }

        return communities;

    }


}
