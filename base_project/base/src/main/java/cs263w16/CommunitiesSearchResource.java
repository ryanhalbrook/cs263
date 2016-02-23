package cs263w16;

import com.google.appengine.api.datastore.*;
import cs263w16.AppDao.AppDaoFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/19/16.
 */
public class CommunitiesSearchResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    DatastoreService _datastore;

    private static final Logger log = Logger.getLogger(CommunitiesResource.class.getName());

    public CommunitiesSearchResource(UriInfo uriInfo, Request request) {

        this.uriInfo = uriInfo;
        this.request = request;

    }

    /*
    Prefix search for communities.
    */
    @POST
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<Community> queryCommunities(@FormParam("prefix") String pattern) {

        return AppDaoFactory.getAppDao().queryCommunitiesPrefix(pattern);

    }

}
