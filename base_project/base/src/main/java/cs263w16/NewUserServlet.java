package cs263w16;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ryanhalbrook on 2/4/16.
 */
public class NewUserServlet extends HttpServlet {
    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String emailAddress = req.getParameter("inputEmail");
        String firstName = req.getParameter("inputFirstName");
        String lastName = req.getParameter("inputLastName");
        String userName = req.getParameter("inputUserName");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find out who the user is.

        String guestbookName = req.getParameter("guestbookName");
        String content = req.getParameter("content");
        if (user != null) {
            AppUser newUser = new AppUser(user.getUserId(), emailAddress, userName, firstName, lastName, new Date());
            // Add the user to the datastore.
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

            if (datastore != null) {
                Entity taskData = new Entity("AppUser", user.getUserId());
                taskData.setProperty("emailAddress", emailAddress);
                taskData.setProperty("userName", userName);
                taskData.setProperty("firstName", firstName);
                taskData.setProperty("lastName", lastName);
                taskData.setProperty("date", newUser.getSignupDate());

                datastore.put(taskData);
            }
        }


        resp.sendRedirect("/");
    }
}
