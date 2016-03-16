package cs263w16.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@Path("/events")
public class EventsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    @Path("{event}")
    public EventResource getEvent(@PathParam("event") String id) {

        if (id == null) {
            return null;
        }

        String regexp = "\\A[\\w]+\\z";

        if (!Pattern.matches(regexp, id)) {
            return null;
        }

        return new EventResource(uriInfo, request, id);

    }

}
