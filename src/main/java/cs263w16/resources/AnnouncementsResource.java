package cs263w16.resources;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import cs263w16.datasources.*;
import cs263w16.model.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.regex.Pattern;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class AnnouncementsResource {
    @Context UriInfo uriInfo;
    @Context Request request;

    private String eventId;

    private EventsDataSource eventsDataSource = new DefaultEventsDataSource();
    private CommunitiesDataSource communitiesDataSource = new DefaultCommunitiesDataSource();
    private MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();

    public AnnouncementsResource(UriInfo uriInfo, Request request, String eventId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.eventId = eventId;
    }


    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAnnouncement(@FormParam("title") String title,
                                    @FormParam("description") String description,
                                    @Context HttpHeaders headers) {

        if (title == null || description == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String regexp = "\\A[a-zA-Z0-9 ]+\\z";

        if (!Pattern.matches(regexp, title) || !Pattern.matches(regexp, description)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userId;

        if (headers.getRequestHeader("userid") != null) {
            userId = headers.getRequestHeader("userid").get(0);
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Get event
        Event event;
        try {
            event = eventsDataSource.getEvent(eventId);
        } catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity("Event not found for event id: " + eventId).build();
        }

        // Get community
        Community community;
        community = communitiesDataSource.getCommunity(event.getCommunityName());
        if (community == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Community not found for community id: " + event.getCommunityName()).build();
        }

        // check - user is the community admin.
        if (!userId.equals(community.getAdminUserId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Announcement announcement = new Announcement(eventId, title, description);
        membershipsDataSource.addAnnouncement(announcement);

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/tasks/propagateannouncementworker").countdownMillis(2000)
                .param("announcementid", eventId + ":" + title));

        return Response.status(Response.Status.CREATED).build();
    }

}
