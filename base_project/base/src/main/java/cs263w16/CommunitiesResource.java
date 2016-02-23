package cs263w16;

import cs263w16.AppDao.AppDaoFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
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

    // Add a new community
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCommunity(@FormParam("communityid") String communityId,
                        @FormParam("description") String description,
                        @Context HttpServletResponse servletResponse) throws IOException {

        Community community = new Community(communityId, description, new Date());
        AppDaoFactory.getAppDao().putCommunity(community);

    }

    // Defines that the next path parameter after communities is
    // treated as a parameter and passed to the CommunityResource
    @Path("{community}")
    public CommunityResource getCommunity(@PathParam("community") String id) {

        return new CommunityResource(uriInfo, request, id);

    }

    @Path("search")
    public CommunitiesSearchResource getCommunitiesSearch() {

        return new CommunitiesSearchResource(uriInfo, request);

    }


}