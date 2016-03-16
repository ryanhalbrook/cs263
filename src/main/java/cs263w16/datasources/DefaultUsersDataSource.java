package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import cs263w16.model.AppUser;
import cs263w16.resources.CommunityResource;

import java.util.logging.*;
import java.io.*;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultUsersDataSource implements UsersDataSource {
    private DatastoreService datastore;
    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();


    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public DefaultUsersDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
        syncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    }

    public AppUser getUser(String userId) throws EntityNotFoundException {

        Key key = KeyFactory.createKey("AppUser", userId);

        Entity entity;

        entity = (Entity)syncCache.get(key.getKind() + "$" + key.getName());

        if (entity == null) {
            entity = datastore.get(key);
            syncCache.put(key.getKind() + "$" + key.getName(), entity);
        }

        return ModelTranslator.unboxAppUser(entity);
    }


    public void addUser(AppUser appUser) throws RedefinitionException, PutFailedException {

        // Check that the user does not already exist
        boolean exists;
        try {
            datastore.get(KeyFactory.createKey("AppUser", appUser.getId()));
            exists = true;
        } catch (EntityNotFoundException e) {
            exists = false;
        }

        if (exists) throw new RedefinitionException();
        Entity userEntity = ModelTranslator.boxAppUser(appUser);

        try {
            datastore.put(userEntity);
        } catch (Exception e) {
            throw new PutFailedException();
        }

    }


}
