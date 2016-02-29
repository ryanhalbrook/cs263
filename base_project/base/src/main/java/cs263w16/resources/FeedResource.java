package cs263w16.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * Created by ryanhalbrook on 2/28/16.
 */
public class FeedResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
}
