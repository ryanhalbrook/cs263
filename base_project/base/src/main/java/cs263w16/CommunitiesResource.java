package cs263w16;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import cs263w16.AppDao.AppDaoFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@Path("/communities")
public class CommunitiesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());

    private static HashMap<String, String> templates = null; // value is base64 encoded html content.


    // Add a new community
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCommunity(@FormParam("communityid") String communityId,
                        @FormParam("description") String description,
                        @Context HttpServletResponse servletResponse) throws IOException
    {
        Community community = new Community(communityId, description, new Date());
        AppDaoFactory.getAppDao(getDatastoreService()).putCommunity(community);
    }

    private DatastoreService getDatastoreService() {
        if (_datastore == null) {
            _datastore = DatastoreServiceFactory.getDatastoreService();
            if (_datastore == null) {
                log.info("Failed to acquire Datastore Service object");
            }
        }
        return _datastore;
    }

    // Defines that the next path parameter after communities is
    // treated as a parameter and passed to the CommunityResource
    @Path("{community}")
    public CommunityResource getTodo(@PathParam("community") String id) {
        return new CommunityResource(uriInfo, request, id);
    }


}