package cs263w16.controllers;

import cs263w16.datasources.CommunitiesDataSource;
import cs263w16.datasources.DefaultCommunitiesDataSource;
import org.junit.BeforeClass;

/**
 * Created by ryanhalbrook on 2/29/16.
 */
public class CommunitiesControllerTest {

    private static CommunitiesDataSource communitiesController;

    @BeforeClass
    public void setup () {
        communitiesController = new DefaultCommunitiesDataSource();
    }

    /*
    @Test
    public void testTest() {

    }
    */
}
