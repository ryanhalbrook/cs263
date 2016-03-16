package cs263w16.datasources;

import com.google.appengine.api.datastore.*;
import cs263w16.model.AppUser;
import cs263w16.resources.CommunityResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultUsersDataSource implements UsersDataSource {
    private DatastoreService datastore;

    private static final Logger log = Logger.getLogger(CommunityResource.class.getName());

    public DefaultUsersDataSource() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public AppUser getUser(String userId) throws EntityNotFoundException {

        Key key = KeyFactory.createKey("AppUser", userId);

        Entity entity = datastore.get(key);
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
