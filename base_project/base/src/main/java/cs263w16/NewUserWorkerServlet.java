package cs263w16;

import cs263w16.datasources.DefaultUsersDataSource;
import cs263w16.datasources.UsersDataSource;
import cs263w16.model.AppUser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ryanhalbrook on 3/2/16.
 */
public class NewUserWorkerServlet extends HttpServlet {

    private static UsersDataSource usersDataSource = new DefaultUsersDataSource();

    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        System.out.println("Do post called");

        String email = req.getParameter("inputEmail");
        String firstname = req.getParameter("inputFirstName");
        String lastname = req.getParameter("inputLastName");
        String username = req.getParameter("inputUserName");

        if (email != null && firstname != null && lastname != null && username != null) {
            // Add the google user as a user of this application.
            AppUser newUser = new AppUser(email, email, username, firstname, lastname, new Date());
            usersDataSource.addUser(newUser);
        }


    }

}
