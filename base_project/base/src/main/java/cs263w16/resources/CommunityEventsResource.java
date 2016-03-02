package cs263w16.resources;


import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.controllers.CommunitiesController;
import cs263w16.controllers.DefaultCommunitiesController;
import cs263w16.controllers.DefaultEventsController;
import cs263w16.controllers.EventsController;
import cs263w16.model.Event;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by ryanhalbrook on 2/12/16.
 */
@Path("/communityevents")
public class CommunityEventsResource {

    @Context UriInfo uriInfo;
    @Context Request request;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private static CommunitiesController communitiesController = new DefaultCommunitiesController();
    private static EventsController eventsController = new DefaultEventsController();

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
        return communitiesController.eventsForCommunity(communityName);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newEvent(@FormParam("name") String name,
                             @FormParam("description") String description,
                             @FormParam("date") String date,
                             @Context HttpServletResponse servletResponse) throws IOException
    {
        Event event = new Event(name, description, communityName, false);
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime dt = fmt.parseDateTime(date);
        event.setEventDate(dt.toDate());
        eventsController.putEvent(event);
    }

}
