package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import javax.xml.bind.JAXBElement;

public class TaskDataResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String keyname;

  private DatastoreService _datastore;
  private MemcacheService _memcache;
  private static final Logger log = Logger.getLogger(TaskDataResource.class.getName());

  public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.keyname = kname;
  }
  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public TaskData getTaskDataHTML() {
    // throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    //if not found
    TaskData taskData = retrieveWithCacheCheck(this.keyname);
    if (taskData == null) {
      throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    }

    return taskData;
  }
  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public TaskData getTaskData() {
    TaskData taskData = retrieveWithCacheCheck(this.keyname);
    if (taskData == null) {
      throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    }

    return taskData;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTaskData(String val) {
    Response res = null;

    TaskData result = retrieve(this.keyname);

    //first check if the Entity exists in the datastore
    if (result == null) {
      // Add the entity to the datastore.
      TaskData taskData = new TaskData(this.keyname, val, new Date());
      put(taskData);

      //signal that we created the entity in the datastore
      res = Response.created(uriInfo.getAbsolutePath()).build();
    } else {
      //update the entity
      result.setValue(val);
      result.setDate(new Date());
      put(result);

      //signal that we updated the entity
      res = Response.noContent().build();
    }

    return res;
  }

  @DELETE
  public void deleteIt() {

    //delete an entity from the datastore
    //just print a message upon exception (don't throw)

    DatastoreService datastore = getDatastoreService();
    MemcacheService memcache = getMemcacheService();

    Key k = KeyFactory.createKey("TaskData", this.keyname);

    try {
      if (memcache != null) {
        String message = memcache.delete(k.getName()) ? "was" : "was not";
        System.out.println("The item " + message + " deleted from the memcache.");
      }
      if (datastore != null) {
        datastore.delete(k);
      }
    } catch (Exception e) {
      System.out.println("Failed to delete object from datastore");
    }

  }

  /**
    Retrieve the Task Data entity with the given keyName. Search in
    1) memcache
    2) datastore

    Adds to memcache if only found in data store.
    @return null if entity not found.
  */
  private TaskData retrieveWithCacheCheck(String keyName) {

    DatastoreService datastore = getDatastoreService();
    MemcacheService memcache = getMemcacheService();
    TaskData taskData = null;

    if (memcache != null) {
      taskData = (TaskData) memcache.get(keyName);
    }

    if (taskData == null) {
      taskData = retrieve(keyName);
      if (taskData != null && memcache != null) {
        memcache.put(keyName, taskData);
      }
    }

    return taskData;

  }

  /**
    Retrieve the Task Data entity with the given keyName. Search in
    1) datastore
    Does not use or manipulate the memcache.

    @return null if entity not found.
  */
  private TaskData retrieve(String keyName) {

    DatastoreService datastore = getDatastoreService();
    if (datastore == null) throw new RuntimeException("Failed to acquire Datastore Service object");

    TaskData taskData = null;
    Key k = KeyFactory.createKey("TaskData", keyName);
    try {
      // Find the entity with the given keyname
      Entity result = datastore.get(k);
      taskData = new TaskData(keyName, (String)result.getProperty("value"), (Date)result.getProperty("date"));

    } catch (EntityNotFoundException e) {
      taskData = null;
    }

    return taskData;
  }

  /**
    Adds or updates (replaces) the entity in the Datastore or the Memcache.
  */
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
      memcache.put(td.getKeyname(), td);
    }
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
