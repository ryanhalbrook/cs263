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
@Path("/communities")
public class CommunitiesResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());
    private static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newCommunity(@FormParam("communityid") String communityId,
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
        if (community != null) {
            return Response.status(Response.Status.CONFLICT).entity("Community already exists with community id: " + communityId).build();
        }

        community = new Community(communityId, description, new Date(), userId);
        communitiesDataSource.addCommunity(community);
        return Response.ok().entity("ok").build();//temporaryRedirect(URI.create("/html/communities.html")).build();
    }

    @Path("{community}")
    public CommunityResource getCommunity(@PathParam("community") String id) {

        return new CommunityResource(uriInfo, request, id);

    }

    @Path("search")
    public CommunitiesSearchResource getCommunitiesSearch() {

        return new CommunitiesSearchResource(uriInfo, request);

    }


}