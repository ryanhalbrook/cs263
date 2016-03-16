package cs263w16.resources;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.model.Community;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 2/19/16.
 */
public class CommunitiesSearchResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());
    private static CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();

    public CommunitiesSearchResource(UriInfo uriInfo, Request request) {

        this.uriInfo = uriInfo;
        this.request = request;

    }

    /*
    Prefix search for communities.
    */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response queryCommunities(@FormParam("prefix") String pattern) {

        System.out.println("Pattern = " + pattern);

        if (pattern == null || pattern.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";

        if (!Pattern.matches(regexp, pattern)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().entity(communitiesDataSource.queryCommunitiesPrefix(pattern)).build();

    }

}
