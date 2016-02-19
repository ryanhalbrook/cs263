package cs263w16;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import cs263w16.AppDao.AppDaoFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/18/16.
 */
public class MembershipResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    private DatastoreService _datastore;
    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String community;

    public MembershipResource(UriInfo uriInfo, Request request, String community) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.community = community;
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public void newMembership(@Context HttpServletResponse servletResponse,
                              @Context HttpHeaders headers) throws IOException
    {
        String user = headers.getRequestHeader("username").get(0);
        AppDaoFactory.getAppDao(getDatastoreService()).addMembership(user, community);
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
