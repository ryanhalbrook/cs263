package cs263w16;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import cs263w16.controllers.DefaultUsersController;
import cs263w16.controllers.UsersController;
import cs263w16.model.AppUser;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ryanhalbrook on 2/4/16.
 */
public class NewUserServlet extends HttpServlet {

    private static UsersController usersController = new DefaultUsersController();

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String emailAddress = req.getParameter("inputEmail");
        String firstName = req.getParameter("inputFirstName");
        String lastName = req.getParameter("inputLastName");
        String userName = req.getParameter("inputUserName");

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (user != null) {

            // Add the google user as a user of this application.

            AppUser newUser = new AppUser(user.getUserId(), emailAddress, user.getUserId(), firstName, lastName, new Date());
            usersController.addUser(newUser);
        }


        resp.sendRedirect("/main.html");
    }
}
