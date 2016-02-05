package cs263w16;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.logging.Level;
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

    private MemcacheService _memcache;
    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String cid;

    public CommunityResource(UriInfo uriInfo, Request request, String communityId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.cid = communityId;
    }

    // for the application
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Community getCommunity() {
        Community community = retrieveWithCacheCheck(this.cid);
        if (community == null) {
            throw new RuntimeException("Get: Community with " + cid +  " not found");
        }

        return community;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putCommunity(String description) {
        Response res = null;

        Community result = retrieve(this.cid);

        //first check if the Entity exists in the datastore
        if (result == null) {
            throw new RuntimeException("Cannot modify a community that does not exist");
        } else {
            //update the entity
            result.setDescription(description);
            put(result);

            //signal that we updated the entity
            res = Response.noContent().build();
        }

        return res;
    }

    @DELETE
    public void deleteIt() {

        //delete an entity from the datastore
        //just print a message upon exception (don't throw)

        DatastoreService datastore = getDatastoreService();
        MemcacheService memcache = getMemcacheService();

        Key k = KeyFactory.createKey("Community", this.cid);

        try {
            if (memcache != null) {
                String message = memcache.delete(k.getName()) ? "was" : "was not";
                System.out.println("The item " + message + " deleted from the memcache.");
            }
            if (datastore != null) {
                datastore.delete(k);
            }
        } catch (Exception e) {
            System.out.println("Failed to delete object from datastore");
        }

    }

    /**
     Retrieve the Community entity with the given keyName. Search in
     1) memcache
     2) datastore

     Adds to memcache if only found in data store.
     @return null if entity not found.
     */
    private Community retrieveWithCacheCheck(String keyName) {

        MemcacheService memcache = getMemcacheService();
        Community community = null;

        if (memcache != null) {
            Entity entity = (Entity) memcache.get(keyName);
            if (entity != null) {
                community = new Community(entity.getKey().getName(), (String) entity.getProperty("description"), (Date) entity.getProperty("date"));
            }
        }

        if (community == null) {
            community = retrieve(keyName);
            if (community != null && memcache != null) {
                Entity entity = new Entity("Community", keyName);
                entity.setProperty("description", community.getDescription());
                entity.setProperty("date", community.getCreationDate());
                memcache.put(keyName, entity);
            }
        }

        return community;

    }

    /**
     Retrieve the Task Data entity with the given keyName. Search in
     1) datastore
     Does not use or manipulate the memcache.

     @return null if entity not found.
     */
    private Community retrieve(String keyName) {

        DatastoreService datastore = getDatastoreService();
        if (datastore == null) throw new RuntimeException("Failed to acquire Datastore Service object");

        Community community = null;
        Key k = KeyFactory.createKey("Community", keyName);
        try {
            // Find the entity with the given keyname
            Entity result = datastore.get(k);
            community = new Community(keyName, (String)result.getProperty("description"), (Date)result.getProperty("date"));

        } catch (EntityNotFoundException e) {
            community = null;
        }

        return community;
    }

    /**
     Adds or updates (replaces) the entity in the Datastore or the Memcache.
     */
    private void put(Community pg) {
        DatastoreService datastore = getDatastoreService();
        MemcacheService memcache = getMemcacheService();

        Entity community = new Entity("Comunity", pg.getId());
        community.setProperty("description", pg.getDescription());
        community.setProperty("date", pg.getCreationDate());

        if (datastore != null) {
            datastore.put(community);
        }
        if (memcache != null) {
            memcache.put(pg.getId(), community);
        }
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

}
