package cs263w16.resources;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.model.Community;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@Path("/community")
public class CommunityResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityName;

    private static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    public CommunityResource(UriInfo uriInfo, Request request, String communityId) {

        this.uriInfo = uriInfo;
        this.request = request;
        this.communityName = communityId;

    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getCommunity() {

        Community community = communitiesDataSource.getCommunity(this.communityName);
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok().entity(community).build();

    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putCommunity(@FormParam("communityid") String communityId,
                                 @FormParam("description") String description,
                                 @Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) throws IOException {

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Community community;

        community = communitiesDataSource.getCommunity(communityId);
        if (community == null) {
            // create a new community.

            community = new Community(communityId, description, new Date(), userId);
            communitiesDataSource.addCommunity(community);


        } else {
            // modify existing community's attributes.
            if (description != null)
                community.setDescription(description);

            communitiesDataSource.updateCommunity(community);

        }

        return Response.temporaryRedirect(URI.create("/html/communities.html")).build();

    }

    @DELETE
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteCommunity(@FormParam("communityid") String communityId, @Context HttpHeaders headers) {


        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Community community = communitiesDataSource.getCommunity(communityId);
        if (community == null) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

        // check - user is the community admin.
        if (!userId.equals(community.getAdminUserId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            communitiesDataSource.deleteCommunity(communityId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }


    @Path("events")
    public CommunityEventsResource getEventsForCommunity(@PathParam("community") String id) {

        return new CommunityEventsResource(uriInfo, request, id);

    }

    /*
    @Path("membership")
    public MembershipsResource getMemberShip(@PathParam("community") String id) {

        return new MembershipsResource(uriInfo, request, id);

    }
    */
}
