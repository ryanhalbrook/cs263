package cs263w16.controllers;

import com.google.appengine.api.datastore.KeyFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.when;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class CommunitiesControllerTest {

    private static CommunitiesController communitiesController;

    @BeforeClass
    public void setup () {
        communitiesController = new DefaultCommunitiesController();
    }

    /*
    @Test
    public void testTest() {

    }
    */
}
