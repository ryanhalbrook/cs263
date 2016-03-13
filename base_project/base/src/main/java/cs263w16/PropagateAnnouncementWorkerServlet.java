package cs263w16;

import com.google.appengine.api.datastore.EntityNotFoundException;
import cs263w16.datasources.DefaultMembershipsDataSource;
import cs263w16.datasources.MembershipsDataSource;
import cs263w16.model.Announcement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class PropagateAnnouncementWorkerServlet extends HttpServlet {

    private MembershipsDataSource membershipsDataSource = new DefaultMembershipsDataSource();

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Announcement announcement;
        String announcementId = req.getParameter("announcementid");
        try {
            System.out.println("AnnouncementId: " + announcementId);
            announcement = membershipsDataSource.getAnnouncement(announcementId);
        } catch (EntityNotFoundException e) {
            System.out.println("Failed to acquire announcement");
            resp.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            return;
        }
        System.out.println("Propagating announcement");
        membershipsDataSource.propagateAnnouncement(announcement);

    }

}
