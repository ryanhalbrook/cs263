package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

// The Worker servlet should be mapped to the "/worker" URL.
public class Worker extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyName = request.getParameter("keyname");
        String value = request.getParameter("value");

        if (keyName == null || value == null) {
          return;
        }

        // Add a TaskData entity to the datastore with keyname and value

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        if (memcache != null) memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));

        if (memcache != null) {
          memcache.put(keyName, value);
        }

        if (datastore != null) {
          Entity taskData = new Entity("TaskData", keyName);
          taskData.setProperty("value", value);
          taskData.setProperty("date", new Date());

          datastore.put(taskData);

        }

    }
}
