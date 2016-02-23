package cs263w16;

import cs263w16.AppDao.AppDaoFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Created by ryanhalbrook on 2/12/16.
 */
@Path("/communityevents")
public class CommunityEventsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String communityName;

    public CommunityEventsResource(UriInfo uriInfo, Request request, String communityName)
    {
        this.uriInfo = uriInfo;
        this.request = request;
        this.communityName = communityName;
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<Event> getEvents()
    {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find out who the user is.
        System.out.println("The user is: " + user);
        return AppDaoFactory.getAppDao().eventsForCommunity(communityName);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCommunity(@FormParam("name") String name,
                             @FormParam("description") String description,
                             @Context HttpServletResponse servletResponse) throws IOException
    {
        Event event = new Event(name, description, communityName, false);
        AppDaoFactory.getAppDao().putEvent(event);
    }

}
