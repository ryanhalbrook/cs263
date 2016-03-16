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
@Path("/communities")
public class CommunitiesResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());
    private static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newCommunity(@FormParam("name") String communityName,
                             @FormParam("description") String description,
                             @Context HttpServletResponse servletResponse,
                             @Context HttpHeaders headers) throws IOException {


        if (communityName == null || description == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";
        String descRegExp = "\\A[a-zA-Z0-9 \\.]+\\z";

        if (!Pattern.matches(regexp, communityName) || !Pattern.matches(descRegExp, description)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Community community = new Community(communityName, description, new Date(), userId);

        Community existingMatch = communitiesDataSource.getCommunity(community.getId());
        if (existingMatch != null) {
            return Response.status(Response.Status.CONFLICT).entity("Community already exists with community id: " + community.getId()).build();
        }

        communitiesDataSource.addCommunity(community);
        return Response.status(Response.Status.CREATED).build();
        //temporaryRedirect(URI.create("/html/communities.html")).build();
    }

    @Path("{community}")
    public CommunityResource getCommunity(@PathParam("community") String id) {

        if (id == null) {
            return null;
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, id)) {
            return null;
        }

        return new CommunityResource(uriInfo, request, id);

    }

    @Path("search")
    public CommunitiesSearchResource getCommunitiesSearch() {

        return new CommunitiesSearchResource(uriInfo, request);

    }


}