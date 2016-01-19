package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.appengine.api.datastore.*;

// The Worker servlet should be mapped to the "/worker" URL.
public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyName = request.getParameter("keyname");
        String value = request.getParameter("value");

        // Add a TaskData entity to the datastore with keyname and value

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        if (datastore != null) {
          Entity taskData = new Entity("TaskData", keyName);
          taskData.setProperty("value", value);
          taskData.setProperty("date", new Date());

          datastore.put(taskData);

        }

    }
}
