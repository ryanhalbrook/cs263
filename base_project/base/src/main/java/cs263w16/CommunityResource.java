package cs263w16;

import com.google.appengine.api.datastore.*;
import cs263w16.AppDao.AppDaoFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@Path("/community")
public class CommunityResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityName;

    public CommunityResource(UriInfo uriInfo, Request request, String communityId)
    {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityName = communityId;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Community getCommunity()
    {
        Community community = AppDaoFactory.getAppDao(getDatastoreService()).getCommunity(this.communityName);
        if (community == null) {
            throw new RuntimeException("Get: Community with " + communityName +  " not found");
        }

        return community;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putCommunity(String description)
    {
        Response res = null;

        Community result = AppDaoFactory.getAppDao(getDatastoreService()).getCommunity(this.communityName);

        //first check if the Entity exists in the datastore
        if (result == null) {
            throw new RuntimeException("Cannot modify a community that does not exist");
        } else {
            //update the entity
            result.setDescription(description);
            AppDaoFactory.getAppDao(getDatastoreService()).putCommunity(result);

            //signal that we updated the entity
            res = Response.noContent().build();
        }

        return res;
    }

    // Delete this community. Fails if their are any events refering to this community.
    @DELETE
    public void deleteIt()
    {
        //delete an entity from the datastore
        //just print a message upon exception (don't throw)

        try {
            AppDaoFactory.getAppDao(getDatastoreService()).deleteCommunity(this.communityName);
        } catch (Exception e) {
            System.out.println("Failed to delete object from datastore");
        }

    }

    private DatastoreService getDatastoreService()
    {
        if (_datastore == null) {
            _datastore = DatastoreServiceFactory.getDatastoreService();
            if (_datastore == null) {
                log.info("Failed to acquire Datastore Service object");
            }
        }
        return _datastore;
    }

    @Path("events")
    public CommunityEventsResource getEventsForCommunity(@PathParam("community") String id)
    {
        return new CommunityEventsResource(uriInfo, request, id);
    }

    @Path("membership")
    public MembershipResource getMemberShip(@PathParam("community") String id)
    {
        return new MembershipResource(uriInfo, request, id);
    }

}
