package cs263w16.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@Path("/events")
public class EventsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    @Path("{event}")
    public EventResource getEvent(@PathParam("event") String id) {

        return new EventResource(uriInfo, request, id);

    }

}
