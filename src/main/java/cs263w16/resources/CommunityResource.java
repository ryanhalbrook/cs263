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
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
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
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response putCommunity(@FormParam("name") String communityName,
                                 @FormParam("description") String description,
                                 @Context HttpServletResponse servletResponse,
                                 @Context HttpHeaders headers) throws IOException {

        if (communityName == null || description == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";

        if (!Pattern.matches(regexp, communityName) || !Pattern.matches(regexp, description)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Community community;

        community = communitiesDataSource.getCommunity(communityName);
        if (community == null) {
            // create a new community.

            community = new Community(communityName, description, new Date(), userId);
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

        if (communityName == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, communityName)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

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
}
