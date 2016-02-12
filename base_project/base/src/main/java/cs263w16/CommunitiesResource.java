package cs263w16;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

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

    private MemcacheService _memcache;
    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());

    private static HashMap<String, String> templates = null; // value is base64 encoded html content.

    // Return the list of entities to applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Community> getEntities() {
        //datastore dump -- only do this if there are a small # of entities
        List<Community> data = dumpDatastore(null);
        return data;
    }

    //Add a new entity to the datastore
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCommunity(@FormParam("communityid") String communityId,
                        @FormParam("description") String description,
                        @Context HttpServletResponse servletResponse) throws IOException
    {
        Community community = new Community(communityId, description, new Date());
        DataModel.putCommunity(getDatastoreService(), community);
    }
/*
    private void put(Community pg) {
        DatastoreService datastore = getDatastoreService();
        MemcacheService memcache = getMemcacheService();

        Entity community = new Entity("Community", pg.getId());
        community.setProperty("description", pg.getDescription());
        community.setProperty("date", pg.getCreationDate());

        if (datastore != null) {
            datastore.put(community);
        }
        if (memcache != null) {
            memcache.put(pg.getId(), community);
        }
    }
*/
    private List<Community> dumpDatastore(List<String> keynames) {
        List<Community> list = new ArrayList<Community>();

        DatastoreService datastore = getDatastoreService();
        if (datastore != null) {
            // Find all entities of kind Community
            Query q = new Query("Community");
            PreparedQuery pq = datastore.prepare(q);

            for (Entity result : pq.asIterable()) {
                String resultValue = (String) result.getProperty("description");
                Date resultDate = (Date) result.getProperty("date");
                list.add(new Community(result.getKey().getName(), resultValue, resultDate));
                if (keynames != null) keynames.add(result.getKey().getName());
            }
        }

        return list;
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

    private MemcacheService getMemcacheService() {
        if (_memcache == null) {
            _memcache = MemcacheServiceFactory.getMemcacheService();
            if (_memcache == null) log.info("Failed to acquire Memcache Service Object");
            else _memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        }
        return _memcache;
    }


    // Defines that the next path parameter after communities is
    // treated as a parameter and passed to the CommunityResource
    @Path("{community}")
    public CommunityResource getTodo(@PathParam("community") String id) {
        return new CommunityResource(uriInfo, request, id);
    }


}