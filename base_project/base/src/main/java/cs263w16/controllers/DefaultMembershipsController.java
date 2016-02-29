package cs263w16.controllers;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class DefaultMembershipsController implements MembershipsController {

    private DatastoreService datastore;

    public DefaultMembershipsController() {
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void addMembership(String user, String community) {
        // TODO add checks.

        System.out.println("Username: " + user);

        Query.Filter keyFilter =
                new Query.FilterPredicate("userName",
                        Query.FilterOperator.EQUAL,
                        user);
        Query q = new Query("AppUser").setFilter(keyFilter);

        List<Entity> entities = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        if (entities.size() != 1) {
            throw new RuntimeException("AppUser not found");
        }

        Entity entity = entities.get(0);
        List<String> memberships = (List<String>)entity.getProperty("memberships");
        if (memberships == null) memberships = new ArrayList<>();
        if (community == null) throw new RuntimeException("Community must be specified");
        if (!memberships.contains(community)) memberships.add(community);
        entity.setProperty("memberships", memberships);

        datastore.put(entity);

    }
}
