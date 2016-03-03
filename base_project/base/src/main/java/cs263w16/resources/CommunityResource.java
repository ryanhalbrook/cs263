package cs263w16.resources;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import cs263w16.model.Community;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
    public Community getCommunity() {

        Community community = communitiesDataSource.getCommunity(this.communityName);
        if (community == null) {
            throw new RuntimeException("Get: Community with " + communityName +  " not found");
        }

        return community;

    }


    /* TODO: modifying an existing community */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putCommunity(String description) {

        Response res = null;

        Community result = communitiesDataSource.getCommunity(this.communityName);

        //first check if the Entity exists in the datastore
        if (result == null) {
            throw new RuntimeException("Cannot modify a community that does not exist");
        } else {
            //update the entity
            result.setDescription(description);
            communitiesDataSource.addCommunity(result);

            //signal that we updated the entity
            res = Response.noContent().build();
        }

        return res;

    }

    @Path("events")
    public CommunityEventsResource getEventsForCommunity(@PathParam("community") String id) {

        return new CommunityEventsResource(uriInfo, request, id);

    }

    @Path("membership")
    public MembershipResource getMemberShip(@PathParam("community") String id) {

        return new MembershipResource(uriInfo, request, id);

    }

}
