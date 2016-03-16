package cs263w16.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class SubscriptionResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private String eventId;

    public SubscriptionResource(UriInfo uriInfo, Request request, String eventId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.eventId = eventId;
    }

}
