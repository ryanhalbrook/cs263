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
@Path("/pages")
public class PagesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private MemcacheService _memcache;
    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(PagesResource.class.getName());

    private static HashMap<String, String> templates = null; // value is base64 encoded html content.

    // Return the list of entities to applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Page> getEntities() {
        //datastore dump -- only do this if there are a small # of entities
        List<Page> data = dumpDatastore(null);
        return data;
    }

    //Add a new entity to the datastore
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newPage(@FormParam("pageid") String pageId,
                            @FormParam("templateid") String templateId,
                            @Context HttpServletResponse servletResponse) throws IOException {
        Date d = new Date();

        if (templates == null) {
            initTemplates();
        }
        if (!templates.containsKey(templateId)) {
            throw new RuntimeException("Get: Template id " + templateId +  " not found");
        }

        System.out.println("Posting new Page with template id = " + ", date = " + d);


        String html = templates.get(templateId);
        Page page = new Page(pageId, html, d);
        put(page);

        //servletResponse.setStatus(204);
        //servletResponse.sendRedirect("/done.html");
    }

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

    private List<Page> dumpDatastore(List<String> keynames) {
        List<Page> list = new ArrayList<Page>();

        DatastoreService datastore = getDatastoreService();
        if (datastore != null) {
            // Find all entities of kind Page
            Query q = new Query("Page");
            PreparedQuery pq = datastore.prepare(q);

            for (Entity result : pq.asIterable()) {
                String resultValue = (String) result.getProperty("html");
                Date resultDate = (Date) result.getProperty("date");
                list.add(new Page(result.getKey().getName(), resultValue, resultDate));
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

    public void initTemplates() {
        templates = new HashMap<>();
        templates.put("1", "PGh0bWw+CiAgICA8Ym9keT4KCiAgICA8ZGl2IHN0eWxlPSJiYWNrZ3JvdW5kLWNvbG9yOmJsYWNrOyBoZWlnaHQ6NzVweDtjb2xvcjp3aGl0ZTsiPgogICAgICAgIDxoMT5BcHAgQnVpbGRlciBCYXNpYzwvaDE+CiAgICA8L2Rpdj4KCiAgICAKICAgIDxkaXYgc3R5bGU9ImhlaWdodDoyMDBweDsiPgogICAgICAgIDxwPgogICAgICAgICAgICBXZWxjb21lLiBUaGlzIGlzIGEgYmFzaWMgdGVtcGxhdGUgdG8gaGVscCB5b3UgZ2V0IHN0YXJ0ZWQuCiAgICAgICAgPC9wPgogICAgPC9kaXY+CiAgICAKICAgIAogICAgPGRpdiBzdHlsZT0iYmFja2dyb3VuZC1jb2xvcjpibGFjazsgaGVpZ2h0OjMwcHg7Y29sb3I6d2hpdGU7Ij4KICAgICAgICA8cD48L3A+CiAgICA8L2Rpdj4KCiAgICA8L2JvZHk+CjwvaHRtbD4=");
    }

    // Defines that the next path parameter after pages is
    // treated as a parameter and passed to the PageResource
    @Path("{page}")
    public PageResource getTodo(@PathParam("page") String id) {
        return new PageResource(uriInfo, request, id);
    }


}
