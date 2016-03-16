package cs263w16;

import cs263w16.datasources.DefaultMembershipsDataSource;
import cs263w16.datasources.MembershipsDataSource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class PropagateEventWorkerServlet extends HttpServlet {

    private MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String eventId = req.getParameter("eventid");
        String communityId = req.getParameter("communityid");

        membershipsDataSource.propagateEvent(eventId, communityId);

    }

}
