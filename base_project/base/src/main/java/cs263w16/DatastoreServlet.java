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

      for (Enumeration<String> paramNames = req.getParameterNames(); paramNames.hasMoreElements();) {
        String paramName = paramNames.nextElement();
        if (!paramName.equals("keyname") &&
            !paramName.equals("value")) {
              resp.getWriter().println("Error, invalid parameters. Only none, keyname, or keyname and value allowed.");
              resp.getWriter().println("</body></html>");
              return;
        }
      }

      String keyName = req.getParameter("keyname");
      String value = req.getParameter("value");

      if (keyName == null && value != null) {
        resp.getWriter().println("Error, invalid parameters. Only none, keyname, or keyname and value allowed.");
        resp.getWriter().println("</body></html>");
        return;
      }

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      if (keyName != null && value != null) {

        resp.getWriter().println("Adding Task Data with Key = " + keyName + " and Value = " + value + " to the Datastore.");

        Entity taskData = new Entity("TaskData", keyName);
        taskData.setProperty("value", value);
        taskData.setProperty("date", new Date());

        datastore.put(taskData);

      } else if (keyName != null) {

        resp.getWriter().println("<h3>The Task Data Entity with Key: " + keyName + "</h3>");

        Key k = KeyFactory.createKey("TaskData", keyName);

        try {

          Entity result = datastore.get(k);
          String resultValue = (String) result.getProperty("value");
          Date resultDate = (Date) result.getProperty("date");

          resp.getWriter().println(resultValue + ", " + resultDate);

        } catch (EntityNotFoundException e) {
          resp.getWriter().println("Could not find the specified entity");
        }

      } else {
        // Find all entities of kind TaskData
        Query q = new Query("TaskData");
        PreparedQuery pq = datastore.prepare(q);

        resp.getWriter().println("<h3>All Task Data Entities</h3>");

        // Display all results
        for (Entity result : pq.asIterable()) {
          String resultValue = (String) result.getProperty("value");
          Date resultDate = (Date) result.getProperty("date");

          resp.getWriter().println("<ul>" + resultValue + ", " + resultDate + "</ul>");
        }
      }


      resp.getWriter().println("</body></html>");
  }
}
