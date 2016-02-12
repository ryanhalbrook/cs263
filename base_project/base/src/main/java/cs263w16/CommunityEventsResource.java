package cs263w16;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/12/16.
 */
@Path("/communityevents")
public class CommunityEventsResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityName;

    public CommunityEventsResource(UriInfo uriInfo, Request request, String communityName) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityName = communityName;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Event> getEvents() {

        return DataModel.eventsForCommunity(getDatastoreService(), communityName);

    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCommunity(@FormParam("name") String name,
                             @FormParam("description") String description,
                             @Context HttpServletResponse servletResponse) throws IOException
    {
        Event event = new Event(name, description, communityName, false);
        DataModel.putEvent(getDatastoreService(), event);
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

}
