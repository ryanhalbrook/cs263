package cs263w16.datasources;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import cs263w16.model.Membership;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Created by ryanhalbrook on 3/2/16.
 */
public class MembershipsDataSourceTest {
    private MembershipsDataSource membershipsDataSource;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()).setEnvIsLoggedIn(true);

    @Before()
    public void setup() {
        membershipsDataSource = new DefaultMembershipsDataSource();
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testAddMembership1() {
        String communityId = "cid";
        String userId = "uid";
        membershipsDataSource.addMembership(userId, communityId);
    }

    @Test
    public void testRemoveMembership1() {
        String communityId = "cid";
        String userId = "uid";
        membershipsDataSource.removeMembership(userId, communityId);
    }

    @Test
    public void testGetMemberships1() {
        List<Membership> memberships = membershipsDataSource.getMemberships("uid");

        assertNull("memberships should be null", memberships); // because the user uid does not exist.
    }

}
