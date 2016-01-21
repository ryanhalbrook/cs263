package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");

      String keyName = req.getParameter("keyname");
      String value = req.getParameter("value");

      // Check that a valid set of parameters is passed in.

      for (Enumeration<String> paramNames = req.getParameterNames(); paramNames.hasMoreElements();) {
        String paramName = paramNames.nextElement();
        if (!paramName.equals("keyname") &&
            !paramName.equals("value")) {
              resp.getWriter().println("Error, invalid parameters. Only none, keyname, or keyname and value allowed.");
              resp.getWriter().println("</body></html>");
              return;
        }
      }

      if (keyName == null && value != null) {
        resp.getWriter().println("Error, invalid parameters. Only none, keyname, or keyname and value allowed.");
        resp.getWriter().println("</body></html>");
        return;
      }




      if (keyName != null && value != null) {

        // Add a TaskData entity to the datastore with keyname and value

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        if (memcache != null) memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));

        if (memcache != null) {
          memcache.put(keyName, value);
          resp.getWriter().println("Stored " + keyName + " and " + value + " in Memcache");
        }

        if (datastore != null) {
          Entity taskData = new Entity("TaskData", keyName);
          taskData.setProperty("value", value);
          taskData.setProperty("date", new Date());

          datastore.put(taskData);

          resp.getWriter().println("Stored " + keyName + " and " + value + " in Datastore");
        }

      } else if (keyName != null) {

        resp.getWriter().println("<h3>Task Data Entity with Key: " + keyName + "</h3>");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        if (memcache != null) memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));

        boolean inMemcache = false;
        boolean inDatastore = false;
        String valueFound = null;

        if (memcache != null) {
          value = (String) memcache.get(keyName);
          inMemcache = (value != null) ? true : false;
        }

        if (datastore != null) {
          Key k = KeyFactory.createKey("TaskData", keyName);

          try {

            // Find the entity with the given keyname

            Entity result = datastore.get(k);
            String resultValue = (String) result.getProperty("value");
            Date resultDate = (Date) result.getProperty("date");

            resp.getWriter().println(k.getName() + ", " + resultValue + ", " + resultDate);

            inDatastore = true;
            valueFound = resultValue;

          } catch (EntityNotFoundException e) {
            resp.getWriter().println("Could not find the specified entity");
            inDatastore = false;
          }
        }

        String message = "";

        if (inMemcache && inDatastore) {
          message = "Both";
        } else if (inDatastore) {
          message = "Datastore";
          // Add to memcache
          if (memcache != null) {
            //memcache.put(keyName, valueFound);
          }

        } else if (inMemcache) {
          message = "Memcache";
        } else {
          message = "Neither";
        }

        resp.getWriter().println("Found in " + message);

      } else {

        ArrayList<String> keyNames = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        if (datastore != null) {
          // Find all entities of kind TaskData
          Query q = new Query("TaskData");
          PreparedQuery pq = datastore.prepare(q);

          resp.getWriter().println("<h3>All Task Data Entities</h3>");

          // Display all results
          resp.getWriter().println("<table border=1>");
          for (Entity result : pq.asIterable()) {
            String resultValue = (String) result.getProperty("value");
            Date resultDate = (Date) result.getProperty("date");
            keyNames.add(result.getKey().getName());

            resp.getWriter().println("<tr><td>" + result.getKey().getName() + "</td><td>" + resultValue + "</td><td>" + resultDate + "</td></tr>");
          }
          resp.getWriter().println("</table>");
        }

        // Show entities that were found in the memcache
        resp.getWriter().println("<h3>Task Data Entities Found in Memcache (based on those seen in the datastore)</h3>");

        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
        if (memcache != null) memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));

        resp.getWriter().println("<table border=1>");
        for (String aKey : keyNames) {
          // Look up in memcache.
          if (memcache != null) {
            Entity entity = (Entity) memcache.get(aKey);
            if (entity != null) {
              resp.getWriter().println("<tr><td>" + aKey + "</td><td>" + entity.getProperty("value") + "</td><td>" + entity.getProperty("date") + "</td></tr>");
            }
          }
        }
        resp.getWriter().println("</table>");

      }


      resp.getWriter().println("</body></html>");
  }
}
