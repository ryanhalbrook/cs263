package cs263w16.AppDao;

import com.google.appengine.api.datastore.DatastoreService;

/**
 * Created by ryanhalbrook on 2/13/16.
 */
public class AppDaoFactory {

    private static AppDao instance;
    public static AppDao getAppDao(DatastoreService datastore) {
        if (instance == null) {
            instance = new Dao(datastore);
        }
        return instance;
    }
}
