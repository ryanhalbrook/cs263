package cs263w16;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class PropagateEventWorkerServlet extends HttpServlet {

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String event = req.getParameter("eventid");
        String community = req.getParameter("communityid");

        // Get all user ids that are

    }

}
