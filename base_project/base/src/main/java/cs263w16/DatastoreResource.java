package cs263w16;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

//Map this class to /ds route
@Path("/ds")
public class DatastoreResource {
  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  private DatastoreService _datastore;
  private MemcacheService _memcache;
  private static final Logger log = Logger.getLogger(TaskDataResource.class.getName());

  // Return the list of entities to the user in the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public List<TaskData> getEntitiesBrowser() {
    //datastore dump -- only do this if there are a small # of entities
    List<String> keynames = new ArrayList<String>();
    List<TaskData> data = dumpDatastore(keynames);

    MemcacheService memcache = getMemcacheService();

    if (memcache != null && keynames != null) {
      for (String keyname : keynames) {
        Entity entity = (Entity) memcache.get(keyname);
        log.info("Found key = " + keyname + " in memcache");
      }
    }

    return data;

  }

  // Return the list of entities to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<TaskData> getEntities() {
    //datastore dump -- only do this if there are a small # of entities
    List<String> keynames = new ArrayList<String>();
    List<TaskData> data = dumpDatastore(keynames);

    MemcacheService memcache = getMemcacheService();

    if (memcache != null && keynames != null) {
      for (String keyname : keynames) {
        Entity entity = (Entity) memcache.get(keyname);
        if (entity != null) {
          log.info("Found key = " + keyname + " in memcache");
        }
      }
    }

    return data;
  }

  //Add a new entity to the datastore
  @POST
  @Produces(MediaType.TEXT_HTML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void newTaskData(@FormParam("keyname") String keyname,
      @FormParam("value") String value,
      @Context HttpServletResponse servletResponse) throws IOException {
    System.out.println("Posting new TaskData: " +keyname+" val: "+value+" ts: "+new Date());
    TaskData taskData = new TaskData(keyname, value, new Date());
    put(taskData);
    
    //servletResponse.setStatus(204);
    //servletResponse.sendRedirect("/done.html");
  }

  //The @PathParam annotation says that keyname can be inserted as parameter after this class's route /ds
  @Path("{keyname}")
  public TaskDataResource getEntity(@PathParam("keyname") String keyname) {
    System.out.println("GETting TaskData for " +keyname);
    return new TaskDataResource(uriInfo, request, keyname);
  }

  private void put(TaskData td) {
    DatastoreService datastore = getDatastoreService();
    MemcacheService memcache = getMemcacheService();

    Entity taskData = new Entity("TaskData", td.getKeyname());
    taskData.setProperty("value", td.getValue());
    taskData.setProperty("date", td.getDate());

    if (datastore != null) {
      datastore.put(taskData);
    }
    if (memcache != null) {
      memcache.put(td.getKeyname(), taskData);
    }
  }

  private List<TaskData> dumpDatastore(List<String> keynames) {
    List<TaskData> list = new ArrayList<TaskData>();

    DatastoreService datastore = getDatastoreService();
    if (datastore != null) {
      // Find all entities of kind TaskData
      Query q = new Query("TaskData");
      PreparedQuery pq = datastore.prepare(q);

      for (Entity result : pq.asIterable()) {
        String resultValue = (String) result.getProperty("value");
        Date resultDate = (Date) result.getProperty("date");
        list.add(new TaskData(result.getKey().getName(), resultValue, resultDate));
        if (keynames != null) keynames.add(result.getKey().getName());
      }
    }

    return list;
  }

  private DatastoreService getDatastoreService() {
    if (_datastore == null) {
      _datastore = DatastoreServiceFactory.getDatastoreService();
      if (_datastore == null) {
        log.info("Failed to acquire Datastore Service object");
      }
    }
    return _datastore;
  }

  private MemcacheService getMemcacheService() {
    if (_memcache == null) {
      _memcache = MemcacheServiceFactory.getMemcacheService();
      if (_memcache == null) log.info("Failed to acquire Memcache Service Object");
      else _memcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    }
    return _memcache;
  }
}
