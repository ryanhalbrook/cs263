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
@Path("/Page")
public class PageResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private MemcacheService _memcache;
    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(PagesResource.class.getName());

    private String pageid;

    public PageResource(UriInfo uriInfo, Request request, String pageid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.pageid = pageid;
    }

    // for the application
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Page getPage() {
        Page page = retrieveWithCacheCheck(this.pageid);
        if (page == null) {
            throw new RuntimeException("Get: Page with " + pageid +  " not found");
        }

        return page;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putPage(String html) {
        Response res = null;

        Page result = retrieve(this.pageid);

        //first check if the Entity exists in the datastore
        if (result == null) {
            throw new RuntimeException("Cannot modify a page that does not exist");
        } else {
            //update the entity
            result.setHtml(html);
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

        Key k = KeyFactory.createKey("Page", this.pageid);

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
     Retrieve the Page entity with the given keyName. Search in
     1) memcache
     2) datastore

     Adds to memcache if only found in data store.
     @return null if entity not found.
     */
    private Page retrieveWithCacheCheck(String keyName) {

        MemcacheService memcache = getMemcacheService();
        Page page = null;

        if (memcache != null) {
            Entity entity = (Entity) memcache.get(keyName);
            if (entity != null) {
                page = new Page(entity.getKey().getName(), (String) entity.getProperty("html"), (Date) entity.getProperty("date"));
            }
        }

        if (page == null) {
            page = retrieve(keyName);
            if (page != null && memcache != null) {
                Entity entity = new Entity("Page", keyName);
                entity.setProperty("html", page.getHtml());
                entity.setProperty("date", page.getCreationDate());
                memcache.put(keyName, entity);
            }
        }

        return page;

    }

    /**
     Retrieve the Task Data entity with the given keyName. Search in
     1) datastore
     Does not use or manipulate the memcache.

     @return null if entity not found.
     */
    private Page retrieve(String keyName) {

        DatastoreService datastore = getDatastoreService();
        if (datastore == null) throw new RuntimeException("Failed to acquire Datastore Service object");

        Page page = null;
        Key k = KeyFactory.createKey("Page", keyName);
        try {
            // Find the entity with the given keyname
            Entity result = datastore.get(k);
            page = new Page(keyName, (String)result.getProperty("html"), (Date)result.getProperty("date"));

        } catch (EntityNotFoundException e) {
            page = null;
        }

        return page;
    }

    /**
     Adds or updates (replaces) the entity in the Datastore or the Memcache.
     */
    private void put(Page pg) {
        DatastoreService datastore = getDatastoreService();
        MemcacheService memcache = getMemcacheService();

        Entity page = new Entity("Page", pg.getId());
        page.setProperty("html", pg.getHtml());
        page.setProperty("date", pg.getCreationDate());

        if (datastore != null) {
            datastore.put(page);
        }
        if (memcache != null) {
            memcache.put(pg.getId(), page);
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
