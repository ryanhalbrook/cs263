package cs263w16.resources;

import cs263w16.controllers.DefaultMembershipsController;
import cs263w16.controllers.MembershipsController;

import javax.servlet.http.HttpServletResponse;
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

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    private String community;

    public static MembershipsController membershipsController = new DefaultMembershipsController();

    public MembershipResource(UriInfo uriInfo, Request request, String community) {

        this.uriInfo = uriInfo;
        this.request = request;
        this.community = community;

    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public void newMembership(@Context HttpServletResponse servletResponse,
                              @Context HttpHeaders headers) throws IOException {

        String user = headers.getRequestHeader("username").get(0);
        membershipsController.addMembership(user, community);

    }


}
