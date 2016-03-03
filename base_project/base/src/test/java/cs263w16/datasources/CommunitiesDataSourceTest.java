package cs263w16.datasources;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import cs263w16.model.Community;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.Date;

/**
 * Created by ryanhalbrook on 2/29/16.
 * Source on Unit Testing App Engine! : https://cloud.google.com/appengine/docs/java/tools/localunittesting
 */
public class CommunitiesDataSourceTest {

    private CommunitiesDataSource communitiesDataSource;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before()
    public void setup() {
        communitiesDataSource = new DefaultCommunitiesDataSource();
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    public void testAddCommunity1() {
        Date date = new Date();
        Community community = new Community("id", "description", date);
        communitiesDataSource.addCommunity(community);
    }

    @Test
    public void testAddCommunity2() {
        Date date = new Date();
        Community community = new Community("", "description", date);
        communitiesDataSource.addCommunity(community);
    }

    @Test
    public void testGetCommunity1() {
        Community community = communitiesDataSource.getCommunity("");
        assertNull(community);
    }

    @Test
    public void testGetCommunity2() {
        Community community = communitiesDataSource.getCommunity("abc");
        assertNull(community);
    }



    @Test
    public void testGetCommunity3() {
        Date date = new Date();
        Community community1 = new Community("id", "description", date);
        communitiesDataSource.addCommunity(community1);
        Community community2 = communitiesDataSource.getCommunity("id");
        assertNotNull(community2);
    }

    @Test
    public void testGetCommunity4() {
        Date date = new Date();
        Community community1 = new Community("id", "description", date);
        communitiesDataSource.addCommunity(community1);
        Community community2 = communitiesDataSource.getCommunity("abc");
        assertNull(community2);
    }

}
