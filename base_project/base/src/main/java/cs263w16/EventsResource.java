package cs263w16;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@Path("/events")
public class EventsResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newEvent(@FormParam("name") String eventName,
                         @FormParam("description") String description,
                         @FormParam("community-name") String communityName,
                         @Context HttpServletResponse servletResponse)
    {
        Event event = new Event(eventName, description, communityName, false);
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
    /*
    // Defines that the next path parameter after communities is
    // treated as a parameter and passed to the CommunityResource
    @Path("{community}")
    public CommunityResource getEventsForCommunity(@PathParam("community") String id) {
        return new CommunityEventsResource(uriInfo, request, id);
    }
    */

}
